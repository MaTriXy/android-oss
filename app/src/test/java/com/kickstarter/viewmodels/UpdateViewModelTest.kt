package com.kickstarter.viewmodels

import android.content.Intent
import android.net.Uri
import android.util.Pair
import com.kickstarter.KSRobolectricTestCase
import com.kickstarter.libs.MockCurrentUserV2
import com.kickstarter.libs.RefTag
import com.kickstarter.libs.utils.NumberUtils
import com.kickstarter.libs.utils.extensions.addToDisposable
import com.kickstarter.mock.factories.ProjectFactory.project
import com.kickstarter.mock.factories.UpdateFactory
import com.kickstarter.mock.factories.UserFactory.creator
import com.kickstarter.mock.services.MockApiClientV2
import com.kickstarter.models.Update
import com.kickstarter.services.ApiClientTypeV2
import com.kickstarter.ui.IntentKey
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.TestScheduler
import io.reactivex.subscribers.TestSubscriber
import okhttp3.Request
import org.junit.After
import org.junit.Test
import java.util.concurrent.TimeUnit

class UpdateViewModelTest : KSRobolectricTestCase() {
    private val defaultIntent = Intent()
        .putExtra(IntentKey.PROJECT, project())
        .putExtra(IntentKey.UPDATE, UpdateFactory.update())

    private val disposables = CompositeDisposable()

    @After
    fun cleanUp() {
        disposables.clear()
    }

    @Test
    fun testOpenProjectExternally_whenProjectUrlIsPreview() {
        val environment = environment()
        val vm = UpdateViewModel.UpdateViewModel(environment)
        val openProjectExternally = TestSubscriber<String>()
        vm.outputs.openProjectExternally().subscribe { openProjectExternally.onNext(it) }
            .addToDisposable(disposables)

        // Start the intent with a project and update.
        vm.provideIntent(defaultIntent)
        val url =
            "https://www.kickstarter.com/projects/smithsonian/smithsonian-anthology-of-hip-hop-and-rap?token=beepboop"
        val projectRequest: Request = Request.Builder()
            .url(url)
            .build()

        vm.inputs.goToProjectRequest(projectRequest)

        openProjectExternally.assertValue("$url&ref=update")
    }

    @Test
    fun testUpdateViewModel_LoadsWebViewUrl() {
        val vm = UpdateViewModel.UpdateViewModel(environment())
        val update = UpdateFactory.update()
        val anotherUpdateUrl = "https://kck.str/projects/param/param/posts/next-id"
        val anotherUpdateRequest: Request = Request.Builder()
            .url(anotherUpdateUrl)
            .build()
        val webViewUrl = TestSubscriber<String>()
        vm.outputs.webViewUrl().subscribe { webViewUrl.onNext(it) }.addToDisposable(disposables)

        // Start the intent with a project and update.
        vm.provideIntent(defaultIntent)

        // Initial update's url emits.
        webViewUrl.assertValues(update.urls()?.web()?.update())

        // Make a request for another update.
        vm.inputs.goToUpdateRequest(anotherUpdateRequest)

        // New update url emits.
        webViewUrl.assertValues(update.urls()?.web()?.update(), anotherUpdateUrl)
    }

    @Test
    fun testUpdateViewModel_StartCommentsActivity() {
        val environment = environment()
        val vm = UpdateViewModel.UpdateViewModel(environment)
        val update = UpdateFactory.update()
        val commentsRequest: Request = Request.Builder()
            .url("https://kck.str/projects/param/param/posts/id/comments")
            .build()
        val startRootCommentsActivity = TestSubscriber<Update>()
        vm.outputs.startRootCommentsActivity().subscribe { startRootCommentsActivity.onNext(it) }
            .addToDisposable(disposables)

        // Start the intent with a project and update.
        vm.provideIntent(
            Intent()
                .putExtra(IntentKey.PROJECT, project())
                .putExtra(IntentKey.UPDATE, update)
        )
        vm.inputs.goToCommentsRequest(commentsRequest)

        startRootCommentsActivity.assertValue(update)
    }

    @Test
    fun testUpdateViewModel_StartProjectActivity() {
        val environment = environment()
        val vm = UpdateViewModel.UpdateViewModel(environment)
        val startProjectActivity = TestSubscriber<Uri>()

        vm.outputs.startProjectActivity()
            .map<Uri> { it.first }
            .subscribe { startProjectActivity.onNext(it) }
            .addToDisposable(disposables)

        // Start the intent with a project and update.
        vm.provideIntent(defaultIntent)

        val url =
            "https://www.kickstarter.com/projects/smithsonian/smithsonian-anthology-of-hip-hop-and-rap"
        val projectRequest: Request = Request.Builder()
            .url(url)
            .build()

        vm.inputs.goToProjectRequest(projectRequest)

        startProjectActivity.assertValues(Uri.parse(url))
    }

    @Test
    fun testUpdateViewModel_whenFeatureFlagOn_shouldEmitProjectPage() {
        val user = MockCurrentUserV2()
        val environment = environment().toBuilder()
            .currentUserV2(user)
            .build()
        val vm = UpdateViewModel.UpdateViewModel(environment)
        val startProjectActivity = TestSubscriber<Pair<Uri, RefTag>>()

        vm.outputs.startProjectActivity().subscribe { startProjectActivity.onNext(it) }
            .addToDisposable(disposables)

        // Start the intent with a project and update.
        vm.provideIntent(defaultIntent)

        val url =
            "https://www.kickstarter.com/projects/smithsonian/smithsonian-anthology-of-hip-hop-and-rap"
        val projectRequest: Request = Request.Builder()
            .url(url)
            .build()

        vm.inputs.goToProjectRequest(projectRequest)

        startProjectActivity.assertValueCount(1)
        assertEquals(startProjectActivity.values().first().first, Uri.parse(url))
        assertEquals(startProjectActivity.values().first().second, RefTag.update())
    }

    @Test
    fun testUpdateViewModel_StartShareIntent() {
        val vm = UpdateViewModel.UpdateViewModel(environment())
        val creator = creator().toBuilder().id(278438049L).build()
        val project = project().toBuilder().creator(creator).build()
        val updatesUrl = "https://www.kck.str/projects/" + project.creator()
            .param() + "/" + project.param() + "/posts"
        val id = 15
        val web = Update.Urls.Web.builder()
            .update("$updatesUrl/$id")
            .likes("$updatesUrl/likes")
            .build()
        val update = UpdateFactory.update()
            .toBuilder()
            .id(id.toLong())
            .projectId(project.id())
            .urls(Update.Urls.builder().web(web).build())
            .build()
        val startShareIntent = TestSubscriber<Pair<Update, String>>()

        vm.outputs.startShareIntent().subscribe { startShareIntent.onNext(it) }
            .addToDisposable(disposables)

        // Start the intent with a project and update.
        vm.provideIntent(
            Intent()
                .putExtra(IntentKey.PROJECT, project())
                .putExtra(IntentKey.UPDATE, update)
        )
        vm.inputs.shareIconButtonClicked()

        val expectedShareUrl = "https://www.kck.str/projects/" + project.creator().param() +
            "/" + project.param() + "/posts/" + id + "?ref=android_update_share"

        startShareIntent.assertValue(Pair.create(update, expectedShareUrl))
    }

    @Test
    fun testUpdateViewModel_UpdateSequence() {
        val initialUpdate = UpdateFactory.update().toBuilder().sequence(1).build()
        val anotherUpdate = UpdateFactory.update().toBuilder().sequence(2).build()
        val anotherUpdateRequest: Request = Request.Builder()
            .url("https://kck.str/projects/param/param/posts/id")
            .build()
        val apiClient: ApiClientTypeV2 = object : MockApiClientV2() {
            override fun fetchUpdate(
                projectParam: String,
                updateParam: String
            ): Observable<Update> {
                return Observable.just(anotherUpdate)
            }
        }
        val environment = environment().toBuilder().apiClientV2(apiClient).build()
        val vm = UpdateViewModel.UpdateViewModel(environment)
        val updateSequence = TestSubscriber<String>()

        vm.outputs.updateSequence().subscribe { updateSequence.onNext(it) }
            .addToDisposable(disposables)

        // Start the intent with a project and update.
        vm.provideIntent(
            Intent()
                .putExtra(IntentKey.PROJECT, project())
                .putExtra(IntentKey.UPDATE, initialUpdate)
        )

        // Initial update's sequence number emits.
        updateSequence.assertValues(NumberUtils.format(initialUpdate.sequence()))
        vm.inputs.goToUpdateRequest(anotherUpdateRequest)

        // New sequence should emit for new update page.
        updateSequence.assertValues(
            NumberUtils.format(initialUpdate.sequence()),
            NumberUtils.format(anotherUpdate.sequence())
        )
    }

    @Test
    fun testUpdateViewModel_WebViewUrl() {
        val vm = UpdateViewModel.UpdateViewModel(environment())
        val update = UpdateFactory.update()
        val webViewUrl = TestSubscriber<String>()
        vm.outputs.webViewUrl().subscribe { webViewUrl.onNext(it) }.addToDisposable(disposables)

        // Start the intent with a project and update.
        vm.provideIntent(
            Intent()
                .putExtra(IntentKey.PROJECT, project())
                .putExtra(IntentKey.UPDATE, update)
        )

        // Initial update index url emits.
        webViewUrl.assertValues(update.urls()?.web()?.update())
    }

    @Test
    fun testUpdateViewModel_DeepLinkPost() {
        val postId = "3254626"
        val update = UpdateFactory.update().toBuilder().sequence(2).build()
        val apiClient: ApiClientTypeV2 = object : MockApiClientV2() {
            override fun fetchUpdate(
                projectParam: String,
                updateParam: String
            ): Observable<Update> {
                return Observable.just(update)
            }
        }
        val environment = environment().toBuilder().apiClientV2(apiClient).build()
        val vm = UpdateViewModel.UpdateViewModel(environment)
        val webViewUrl = TestSubscriber<String>()
        vm.outputs.webViewUrl().subscribe { webViewUrl.onNext(it) }.addToDisposable(disposables)

        // Start the intent with a project and update.
        vm.provideIntent(
            Intent()
                .putExtra(IntentKey.PROJECT, project())
                .putExtra(IntentKey.UPDATE_POST_ID, postId)
        )

        // Initial update index url emits.
        webViewUrl.assertValues(update.urls()?.web()?.update())
    }

    @Test
    fun testUpdateViewModel_DeepLinkComment() {
        val postId = "3254626"
        val update = UpdateFactory.update()
        val apiClient: ApiClientTypeV2 = object : MockApiClientV2() {
            override fun fetchUpdate(
                projectParam: String,
                updateParam: String
            ): Observable<Update> {
                return Observable.just(update)
            }
        }
        val testScheduler = TestScheduler()
        val environment =
            environment().toBuilder().apiClientV2(apiClient).schedulerV2(testScheduler).build()
        val vm = UpdateViewModel.UpdateViewModel(environment)
        val startRootCommentsActivity = TestSubscriber<Update>()
        vm.outputs.startRootCommentsActivity().subscribe { startRootCommentsActivity.onNext(it) }
            .addToDisposable(disposables)
        val webViewUrl = TestSubscriber<String>()
        vm.outputs.webViewUrl().subscribe { webViewUrl.onNext(it) }.addToDisposable(disposables)
        val deepLinkToRootComment = TestSubscriber<Boolean>()
        vm.hasCommentsDeepLinks().subscribe { deepLinkToRootComment.onNext(it) }
            .addToDisposable(disposables)

        // Start the intent with a project and update.
        vm.provideIntent(
            Intent()
                .putExtra(IntentKey.PROJECT, project())
                .putExtra(IntentKey.UPDATE_POST_ID, postId)
                .putExtra(IntentKey.IS_UPDATE_COMMENT, true)
        )

        // Initial update index url emits.
        webViewUrl.assertValues(update.urls()?.web()?.update())
        deepLinkToRootComment.assertValue(true)

        vm.inputs.goToCommentsActivity()

        testScheduler.advanceTimeBy(2, TimeUnit.SECONDS)
        startRootCommentsActivity.assertValue(update)
        startRootCommentsActivity.assertValueCount(1)
    }

    @Test
    fun testUpdateViewModel_DeepLinkCommentThread() {
        val postId = "3254626"
        val commentableId = "Q29tbWVudC0zMzU2MTY4Ng"
        val update = UpdateFactory.update()
        val apiClient: ApiClientTypeV2 = object : MockApiClientV2() {
            override fun fetchUpdate(
                projectParam: String,
                updateParam: String
            ): Observable<Update> {
                return Observable.just(update)
            }
        }
        val testScheduler = TestScheduler()
        val environment =
            environment().toBuilder().apiClientV2(apiClient).schedulerV2(testScheduler).build()
        val vm = UpdateViewModel.UpdateViewModel(environment)
        val startRootCommentsActivityToDeepLinkThreadActivity =
            TestSubscriber<Pair<String, Update>>()
        vm.outputs.startRootCommentsActivityToDeepLinkThreadActivity()
            .subscribe { startRootCommentsActivityToDeepLinkThreadActivity.onNext(it) }
            .addToDisposable(disposables)
        val webViewUrl = TestSubscriber<String>()
        vm.outputs.webViewUrl().subscribe { webViewUrl.onNext(it) }.addToDisposable(disposables)
        val deepLinkToThreadActivity = TestSubscriber<Pair<String, Boolean>>()
        vm.deepLinkToThreadActivity().subscribe { deepLinkToThreadActivity.onNext(it) }
            .addToDisposable(disposables)

        // Start the intent with a project and update.
        vm.provideIntent(
            Intent()
                .putExtra(IntentKey.PROJECT, project())
                .putExtra(IntentKey.UPDATE_POST_ID, postId)
                .putExtra(IntentKey.IS_UPDATE_COMMENT, true)
                .putExtra(IntentKey.COMMENT, commentableId)
        )

        // Initial update index url emits.
        webViewUrl.assertValues(update.urls()?.web()?.update())
        deepLinkToThreadActivity.assertValue(Pair.create(commentableId, true))

        vm.inputs.goToCommentsActivityToDeepLinkThreadActivity(commentableId)

        testScheduler.advanceTimeBy(2, TimeUnit.SECONDS)

        startRootCommentsActivityToDeepLinkThreadActivity.assertValue(
            Pair.create(
                commentableId,
                update
            )
        )
        startRootCommentsActivityToDeepLinkThreadActivity.assertValueCount(1)
    }
}
