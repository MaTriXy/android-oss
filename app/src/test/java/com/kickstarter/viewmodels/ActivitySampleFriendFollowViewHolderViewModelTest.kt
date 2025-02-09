package com.kickstarter.viewmodels

import com.kickstarter.KSRobolectricTestCase
import com.kickstarter.libs.utils.extensions.addToDisposable
import com.kickstarter.mock.factories.ProjectFactory
import com.kickstarter.mock.factories.UserFactory
import com.kickstarter.models.Activity
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subscribers.TestSubscriber
import org.joda.time.DateTime
import org.junit.After
import org.junit.Test

class ActivitySampleFriendFollowViewHolderViewModelTest : KSRobolectricTestCase() {
    private lateinit var vm: ActivitySampleFriendFollowViewHolderViewModel.ViewModel

    private val bindActivity = TestSubscriber.create<Activity>()

    private val disposables = CompositeDisposable()

    private fun setupEnvironment() {
        this.vm = ActivitySampleFriendFollowViewHolderViewModel.ViewModel()
        this.vm.outputs.bindActivity().subscribe { this.bindActivity.onNext(it) }
            .addToDisposable(disposables)
    }

    @Test
    fun testBindActivityWithoutProjectAndUser() {
        setupEnvironment()
        this.vm.inputs.configureWith(
            Activity.builder()
                .category(Activity.CATEGORY_FOLLOW)
                .createdAt(DateTime.now())
                .updatedAt(DateTime.now())
                .id(1).build()
        )
        this.bindActivity.assertNoValues()
    }

    @Test
    fun testBindActivityHasProjectAndUser() {
        setupEnvironment()

        val activityWithProjectAndUser = Activity.builder()
            .category(Activity.CATEGORY_FOLLOW)
            .project(ProjectFactory.backedProject())
            .user(UserFactory.creator())
            .createdAt(DateTime.now())
            .updatedAt(DateTime.now())
            .id(1).build()

        this.vm.inputs.configureWith(activityWithProjectAndUser)

        this.bindActivity.assertValue(activityWithProjectAndUser)
    }

    @After
    fun clear() {
        disposables.clear()
    }
}
