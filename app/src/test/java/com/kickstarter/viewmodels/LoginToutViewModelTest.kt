package com.kickstarter.viewmodels

import com.facebook.FacebookAuthorizationException
import com.kickstarter.KSRobolectricTestCase
import com.kickstarter.libs.Environment
import com.kickstarter.libs.MockCurrentUserV2
import com.kickstarter.libs.featureflag.FlagKey
import com.kickstarter.libs.utils.EventName
import com.kickstarter.libs.utils.extensions.addToDisposable
import com.kickstarter.mock.MockFeatureFlagClient
import com.kickstarter.mock.factories.ApiExceptionFactory
import com.kickstarter.mock.factories.UserFactory
import com.kickstarter.mock.services.MockApiClientV2
import com.kickstarter.mock.services.MockApolloClientV2
import com.kickstarter.models.User
import com.kickstarter.models.UserPrivacy
import com.kickstarter.services.apiresponses.AccessTokenEnvelope
import com.kickstarter.services.apiresponses.ErrorEnvelope
import com.kickstarter.ui.activities.DisclaimerItems
import com.kickstarter.ui.data.LoginReason
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subscribers.TestSubscriber
import org.junit.Test

class LoginToutViewModelTest : KSRobolectricTestCase() {
    private lateinit var vm: LoginToutViewModel.LoginToutViewmodel
    private val finishWithSuccessfulResult = TestSubscriber<Unit>()
    private val finishOathWithSuccessfulResult = TestSubscriber<Unit>()
    private val loginError = TestSubscriber<ErrorEnvelope>()
    private val startLoginActivity = TestSubscriber<Unit>()
    private val currentUser = TestSubscriber<User?>()
    private val showDisclaimerActivity = TestSubscriber<DisclaimerItems>()
    private val startResetPasswordActivity = TestSubscriber<Unit>()
    private val showFacebookErrorDialog = TestSubscriber<Unit>()

    private val disposables = CompositeDisposable()

    private fun setUpEnvironment(environment: Environment, loginReason: LoginReason) {
        vm = LoginToutViewModel.LoginToutViewmodel(environment)
        vm.outputs.finishWithSuccessfulResult().subscribe { finishWithSuccessfulResult.onNext(it) }
            .addToDisposable(disposables)
        vm.loginError.subscribe { loginError.onNext(it) }.addToDisposable(disposables)
        vm.outputs.startLoginActivity().subscribe { startLoginActivity.onNext(it) }
            .addToDisposable(disposables)
        vm.outputs.showFacebookErrorDialog().subscribe { showFacebookErrorDialog.onNext(it) }
            .addToDisposable(disposables)
        vm.outputs.startResetPasswordActivity().subscribe { startResetPasswordActivity.onNext(it) }
            .addToDisposable(disposables)
        vm.outputs.showDisclaimerActivity().subscribe { showDisclaimerActivity.onNext(it) }
            .addToDisposable(disposables)
        environment.currentUserV2()?.observable()?.subscribe { currentUser.onNext(it.getValue()) }
            ?.addToDisposable(disposables)
        vm.outputs.finishOauthWithSuccessfulResult().subscribe {
            finishOathWithSuccessfulResult.onNext(it)
        }
            .addToDisposable(disposables)

        vm.provideLoginReason(loginReason)
    }

    @Test
    fun facebookLogin_success() {
        val currentUser = MockCurrentUserV2()
        val userFb = UserFactory.user()
        val apiClient = object : MockApiClientV2() {
            override fun loginWithFacebook(accessToken: String): Observable<AccessTokenEnvelope> {
                return Observable.just(AccessTokenEnvelope.builder().user(userFb).accessToken("token").build())
            }
        }

        val apolloClient = object : MockApolloClientV2() {
            override fun userPrivacy(): Observable<UserPrivacy> {
                return Observable.just(
                    UserPrivacy(userFb.name(), "some@email.com", true, true, true, true, "USD")
                )
            }
        }

        val environment = environment()
            .toBuilder()
            .apiClientV2(apiClient)
            .apolloClientV2(apolloClient)
            .currentUserV2(currentUser)
            .build()

        setUpEnvironment(environment, LoginReason.DEFAULT)

        this.currentUser.values().clear()

        vm.inputs.facebookLoginClick(
            null,
            listOf("public_profile", "user_friends", "email")
        )

        vm.facebookAccessToken.onNext("token")

        this.currentUser.assertValueCount(2)
        finishWithSuccessfulResult.assertValueCount(1)

        assertEquals("some@email.com", this.currentUser.values().last()?.email())
        segmentTrack.assertValues(EventName.PAGE_VIEWED.eventName, EventName.CTA_CLICKED.eventName)
    }

    @Test
    fun facebookLogin_error_reset_password_WithFeatureFlag_Enabled() {
        val currentUser = MockCurrentUserV2()
        val mockFeatureFlagClientType: MockFeatureFlagClient =
            object : MockFeatureFlagClient() {
                override fun getBoolean(FlagKey: FlagKey): Boolean {
                    return true
                }
            }

        val environment = environment()
            .toBuilder()
            .currentUserV2(currentUser)
            .featureFlagClient(mockFeatureFlagClientType)
            .build()
        setUpEnvironment(environment, LoginReason.DEFAULT)

        vm.inputs.facebookLoginClick(
            null,
            listOf("public_profile", "user_friends", "email")
        )

        vm.facebookAuthorizationError.onNext(FacebookAuthorizationException())

        finishWithSuccessfulResult.assertNoValues()
        showFacebookErrorDialog.assertValueCount(1)

        vm.inputs.onResetPasswordFacebookErrorDialogClicked()

        startLoginActivity.assertNoValues()
        startResetPasswordActivity.assertValueCount(1)
        segmentTrack.assertValues(EventName.PAGE_VIEWED.eventName, EventName.CTA_CLICKED.eventName)
    }

    @Test
    fun facebookLogin_error_login_WithFeatureFlag_Enabled() {
        val currentUser = MockCurrentUserV2()
        val mockFeatureFlagClient: MockFeatureFlagClient =
            object : MockFeatureFlagClient() {
                override fun getBoolean(FlagKey: FlagKey): Boolean {
                    return true
                }
            }

        val environment = environment()
            .toBuilder()
            .currentUserV2(currentUser)
            .featureFlagClient(mockFeatureFlagClient)
            .apiClientV2(object : MockApiClientV2() {
                override fun loginWithFacebook(accessToken: String): Observable<AccessTokenEnvelope> {
                    return Observable.error(
                        ApiExceptionFactory.apiError(
                            ErrorEnvelope.builder().httpCode(400).build()
                        )
                    )
                }
            })
            .build()
        setUpEnvironment(environment, LoginReason.DEFAULT)

        this.currentUser.values().clear()

        vm.inputs.facebookLoginClick(
            null,
            listOf("public_profile", "user_friends", "email")
        )

        vm.facebookAccessToken.onNext("token")

        this.currentUser.assertNoValues()
        finishWithSuccessfulResult.assertNoValues()
        showFacebookErrorDialog.assertValueCount(0)
    }

    @Test
    fun facebookLogin_error() {
        val currentUser = MockCurrentUserV2()
        val environment = environment()
            .toBuilder()
            .currentUserV2(currentUser)
            .apiClientV2(object : MockApiClientV2() {
                override fun loginWithFacebook(accessToken: String): Observable<AccessTokenEnvelope> {
                    return Observable.error(Throwable("error"))
                }
            })
            .build()
        setUpEnvironment(environment, LoginReason.DEFAULT)

        this.currentUser.values().clear()

        vm.inputs.facebookLoginClick(
            null,
            listOf("public_profile", "user_friends", "email")
        )

        vm.facebookAccessToken.onNext("token")

        this.currentUser.assertNoValues()
        finishWithSuccessfulResult.assertNoValues()
        startResetPasswordActivity.assertNoValues()
        segmentTrack.assertValues(EventName.PAGE_VIEWED.eventName, EventName.CTA_CLICKED.eventName)
    }

    @Test
    fun testTermsDisclaimerClicked() {
        setUpEnvironment(environment(), LoginReason.DEFAULT)

        showDisclaimerActivity.assertNoValues()

        vm.inputs.disclaimerItemClicked(DisclaimerItems.TERMS)
        showDisclaimerActivity.assertValue(DisclaimerItems.TERMS)
    }

    @Test
    fun testPrivacyDisclaimerClicked() {
        setUpEnvironment(environment(), LoginReason.DEFAULT)

        showDisclaimerActivity.assertNoValues()

        vm.inputs.disclaimerItemClicked(DisclaimerItems.PRIVACY)
        showDisclaimerActivity.assertValue(DisclaimerItems.PRIVACY)
    }

    @Test
    fun testCookiesDisclaimerClicked() {
        setUpEnvironment(environment(), LoginReason.DEFAULT)

        showDisclaimerActivity.assertNoValues()

        vm.inputs.disclaimerItemClicked(DisclaimerItems.COOKIES)
        showDisclaimerActivity.assertValue(DisclaimerItems.COOKIES)
    }
}
