package com.kickstarter.viewmodels

import android.util.Pair
import com.kickstarter.KSRobolectricTestCase
import com.kickstarter.R
import com.kickstarter.libs.utils.NumberUtils
import com.kickstarter.libs.utils.ProgressBarUtils
import com.kickstarter.libs.utils.extensions.addToDisposable
import com.kickstarter.libs.utils.extensions.isNull
import com.kickstarter.mock.factories.CategoryFactory.artCategory
import com.kickstarter.mock.factories.CategoryFactory.bluesCategory
import com.kickstarter.mock.factories.CategoryFactory.category
import com.kickstarter.mock.factories.CategoryFactory.ceramicsCategory
import com.kickstarter.mock.factories.ProjectFactory.project
import com.kickstarter.mock.factories.ProjectFactory.staffPick
import com.kickstarter.mock.factories.UserFactory.user
import com.kickstarter.models.Project
import com.kickstarter.models.User
import com.kickstarter.services.DiscoveryParams
import com.kickstarter.services.DiscoveryParams.Companion.builder
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subscribers.TestSubscriber
import org.joda.time.DateTime
import org.junit.After
import org.junit.Test
import java.util.Arrays

class ProjectCardHolderViewModelTest : KSRobolectricTestCase() {
    private lateinit var vm: ProjectCardHolderViewModel.ViewModel
    private val backersCountTextViewText = TestSubscriber<String>()
    private val backingViewGroupIsGone = TestSubscriber<Boolean>()
    private val deadlineCountdownText = TestSubscriber<String>()
    private val featuredViewGroupIsGone = TestSubscriber<Boolean>()
    private val friendAvatar2IsHidden = TestSubscriber<Boolean>()
    private val friendAvatar3IsHidden = TestSubscriber<Boolean>()
    private val friendAvatarUrl1 = TestSubscriber<String>()
    private val friendAvatarUrl2 = TestSubscriber<String>()
    private val friendAvatarUrl3 = TestSubscriber<String>()
    private val friendBackingViewIsHidden = TestSubscriber<Boolean>()
    private val friendsForNamepile = TestSubscriber<List<User>>()
    private val fundingUnsuccessfulViewGroupIsGone = TestSubscriber<Boolean>()
    private val fundingSuccessfulViewGroupIsGone = TestSubscriber<Boolean>()
    private val imageIsInvisible = TestSubscriber<Boolean>()
    private val locationContainerIsGone = TestSubscriber<Boolean>()
    private val locationName = TestSubscriber<String>()
    private val metadataViewGroupBackgroundDrawable = TestSubscriber<Int>()
    private val metadataViewGroupIsGone = TestSubscriber<Boolean>()
    private val nameAndBlurbText = TestSubscriber<Pair<String, String>>()
    private val notifyDelegateOfProjectClick = TestSubscriber<Project>()
    private val percentageFundedForProgressBar = TestSubscriber<Int>()
    private val percentageFundedTextViewText = TestSubscriber<String>()
    private val photoUrl = TestSubscriber<String>()
    private val projectCanceledAt = TestSubscriber<DateTime?>()
    private val projectCardStatsViewGroupIsGone = TestSubscriber<Boolean>()
    private val projectFailedAt = TestSubscriber<DateTime?>()
    private val projectStateViewGroupIsGone = TestSubscriber<Boolean>()
    private val projectSubcategoryIsGone = TestSubscriber<Boolean>()
    private val projectSubcategoryName = TestSubscriber<String>()
    private val projectSuccessfulAt = TestSubscriber<DateTime?>()
    private val projectSuspendedAt = TestSubscriber<DateTime?>()
    private val projectTagContainerIsGone = TestSubscriber<Boolean>()
    private val projectWeLoveIsGone = TestSubscriber<Boolean>()
    private val rootCategoryNameForFeatured = TestSubscriber<String>()
    private val setDefaultTopPadding = TestSubscriber<Boolean>()
    private val savedViewGroupIsGone = TestSubscriber<Boolean>()
    private val comingSoonViewGroupIsGone = TestSubscriber<Boolean>()
    private val heartDrawableId = TestSubscriber<Int>()
    private val notifyDelegateOfHeartButtonClicked = TestSubscriber<Project>()
    private val disposables = CompositeDisposable()

    private fun setUpEnvironment() {
        vm = ProjectCardHolderViewModel.ViewModel()
        vm.outputs.backersCountTextViewText().subscribe { backersCountTextViewText.onNext(it) }
            .addToDisposable(disposables)
        vm.outputs.backingViewGroupIsGone().subscribe { backingViewGroupIsGone.onNext(it) }
            .addToDisposable(disposables)
        vm.outputs.deadlineCountdownText().subscribe { deadlineCountdownText.onNext(it) }
            .addToDisposable(disposables)
        vm.outputs.featuredViewGroupIsGone().subscribe { featuredViewGroupIsGone.onNext(it) }
            .addToDisposable(disposables)
        vm.outputs.friendBackingViewIsHidden().subscribe { friendBackingViewIsHidden.onNext(it) }
            .addToDisposable(disposables)
        vm.outputs.friendAvatar2IsGone().subscribe { friendAvatar2IsHidden.onNext(it) }
            .addToDisposable(disposables)
        vm.outputs.friendAvatar3IsGone().subscribe { friendAvatar3IsHidden.onNext(it) }
            .addToDisposable(disposables)
        vm.outputs.friendAvatarUrl1().subscribe { friendAvatarUrl1.onNext(it) }
            .addToDisposable(disposables)
        vm.outputs.friendAvatarUrl2().subscribe { friendAvatarUrl2.onNext(it) }
            .addToDisposable(disposables)
        vm.outputs.friendAvatarUrl3().subscribe { friendAvatarUrl3.onNext(it) }
            .addToDisposable(disposables)
        vm.outputs.friendsForNamepile().subscribe { friendsForNamepile.onNext(it) }
            .addToDisposable(disposables)
        vm.outputs.fundingUnsuccessfulViewGroupIsGone().subscribe {
            fundingUnsuccessfulViewGroupIsGone.onNext(it)
        }.addToDisposable(disposables)
        vm.outputs.fundingSuccessfulViewGroupIsGone()
            .subscribe { fundingSuccessfulViewGroupIsGone.onNext(it) }.addToDisposable(disposables)
        vm.outputs.imageIsInvisible().subscribe { imageIsInvisible.onNext(it) }
            .addToDisposable(disposables)
        vm.outputs.locationContainerIsGone().subscribe { locationContainerIsGone.onNext(it) }
            .addToDisposable(disposables)
        vm.outputs.locationName().subscribe { locationName.onNext(it) }.addToDisposable(disposables)
        vm.outputs.metadataViewGroupBackgroundDrawable().subscribe {
            metadataViewGroupBackgroundDrawable.onNext(it)
        }.addToDisposable(disposables)
        vm.outputs.metadataViewGroupIsGone().subscribe { metadataViewGroupIsGone.onNext(it) }
            .addToDisposable(disposables)
        vm.outputs.nameAndBlurbText().subscribe { nameAndBlurbText.onNext(it) }
            .addToDisposable(disposables)
        vm.outputs.notifyDelegateOfProjectClick()
            .subscribe { notifyDelegateOfProjectClick.onNext(it) }.addToDisposable(disposables)
        vm.outputs.percentageFundedForProgressBar()
            .subscribe { percentageFundedForProgressBar.onNext(it) }.addToDisposable(disposables)
        vm.outputs.percentageFundedTextViewText()
            .subscribe { percentageFundedTextViewText.onNext(it) }.addToDisposable(disposables)
        vm.outputs.photoUrl().subscribe { photoUrl.onNext(it) }.addToDisposable(disposables)
        vm.outputs.projectCanceledAt().subscribe { projectCanceledAt.onNext(it) }
            .addToDisposable(disposables)
        vm.outputs.projectCardStatsViewGroupIsGone()
            .subscribe { projectCardStatsViewGroupIsGone.onNext(it) }.addToDisposable(disposables)
        vm.outputs.projectFailedAt().subscribe { projectFailedAt.onNext(it) }
            .addToDisposable(disposables)
        vm.outputs.projectStateViewGroupIsGone()
            .subscribe { projectStateViewGroupIsGone.onNext(it) }.addToDisposable(disposables)
        vm.outputs.projectSubcategoryIsGone().subscribe { projectSubcategoryIsGone.onNext(it) }
            .addToDisposable(disposables)
        vm.outputs.projectSubcategoryName().subscribe { projectSubcategoryName.onNext(it) }
            .addToDisposable(disposables)
        vm.outputs.projectSuccessfulAt().subscribe { projectSuccessfulAt.onNext(it) }
            .addToDisposable(disposables)
        vm.outputs.projectSuspendedAt().subscribe { projectSuspendedAt.onNext(it) }
            .addToDisposable(disposables)
        vm.outputs.projectTagContainerIsGone().subscribe { projectTagContainerIsGone.onNext(it) }
            .addToDisposable(disposables)
        vm.outputs.projectWeLoveIsGone().subscribe { projectWeLoveIsGone.onNext(it) }
            .addToDisposable(disposables)
        vm.outputs.rootCategoryNameForFeatured()
            .subscribe { rootCategoryNameForFeatured.onNext(it) }.addToDisposable(disposables)
        vm.outputs.setDefaultTopPadding().subscribe { setDefaultTopPadding.onNext(it) }
            .addToDisposable(disposables)
        vm.outputs.savedViewGroupIsGone().subscribe { savedViewGroupIsGone.onNext(it) }
            .addToDisposable(disposables)
        vm.outputs.comingSoonViewGroupIsGone().subscribe { comingSoonViewGroupIsGone.onNext(it) }
            .addToDisposable(disposables)
        vm.outputs.heartDrawableId().subscribe { heartDrawableId.onNext(it) }
            .addToDisposable(disposables)
        vm.outputs.notifyDelegateOfHeartButtonClicked().subscribe {
            notifyDelegateOfHeartButtonClicked.onNext(it)
        }.addToDisposable(disposables)
    }

    @Test
    fun testProjectIsStarred() {
        val project = project().toBuilder().isStarred(true).build()
        setUpEnvironment()

        vm.inputs.configureWith(Pair.create(project, builder().build()))

        heartDrawableId.assertValue(R.drawable.icon__heart)
    }

    @Test
    fun testProjectIsNotStarred() {
        val project = project().toBuilder().isStarred(false).build()
        setUpEnvironment()

        vm.inputs.configureWith(Pair.create(project, builder().build()))

        heartDrawableId.assertValue(R.drawable.icon__heart_outline)
    }

    @Test
    fun testNotifyDelegateOfHeartButtonClickedClick() {
        val project = project()
        setUpEnvironment()

        vm.inputs.configureWith(Pair.create(project, builder().build()))
        vm.inputs.heartButtonClicked()

        notifyDelegateOfHeartButtonClicked.assertValues(project)
    }

    @Test
    fun testEmitsBackersCountTextViewText() {
        val project = project().toBuilder().backersCount(50).build()
        setUpEnvironment()

        vm.inputs.configureWith(Pair.create(project, builder().build()))

        backersCountTextViewText.assertValues(NumberUtils.format(50))
    }

    @Test
    fun testBackingViewGroupIsGone_isBacking() {
        val project = project().toBuilder().isBacking(true).build()
        setUpEnvironment()

        vm.inputs.configureWith(Pair.create(project, builder().build()))

        backingViewGroupIsGone.assertValues(false)
    }

    @Test
    fun testBackingViewGroupIsGone_isStarred() {
        val project = project()
            .toBuilder()
            .isBacking(false)
            .isStarred(false)
            .featuredAt(null)
            .build()
        setUpEnvironment()

        vm.inputs.configureWith(Pair.create(project, builder().build()))

        backingViewGroupIsGone.assertValues(true)
    }

    @Test
    fun testEmitsDeadlineCountdownText() {
        val project = project().toBuilder()
            .deadline(DateTime().plusSeconds(60 * 60 * 24 + 1))
            .build()
        setUpEnvironment()

        vm.inputs.configureWith(Pair.create(project, builder().build()))

        deadlineCountdownText.assertValues("24")
    }

    /*@Test
  public void testFeaturedViewGroupIsGone_isBacking() {
    final Project project = ProjectFactory.project().toBuilder().isBacking(true).build();
    setUpEnvironment();

    this.vm.inputs.configureWith(Pair.create(project, DiscoveryParams.builder().build()));
    this.featuredViewGroupIsGone.assertValues(true);
  }*/
    @Test
    fun testFeaturedViewGroupIsGone_isFeatured() {
        val project = project().toBuilder().featuredAt(DateTime.now()).build()
        setUpEnvironment()

        vm.inputs.configureWith(Pair.create(project, builder().build()))

        featuredViewGroupIsGone.assertValues(false)
    }

    @Test
    fun testFriendAvatarUrl_withOneFriend() {
        val project = project()
            .toBuilder()
            .friends(listOf(user()))
            .build()
        setUpEnvironment()

        vm.inputs.configureWith(Pair.create(project, builder().build()))

        friendAvatarUrl1.assertValues(project.friends()[0].avatar().small())
        friendAvatarUrl2.assertNoValues()
        friendAvatarUrl3.assertNoValues()
        friendAvatar2IsHidden.assertValue(true)
        friendAvatar3IsHidden.assertValue(true)
    }

    @Test
    fun testFriendAvatarUrl_withTwoFriends() {
        val project = project()
            .toBuilder()
            .friends(Arrays.asList(user(), user()))
            .build()
        setUpEnvironment()

        vm.inputs.configureWith(Pair.create(project, builder().build()))

        friendAvatarUrl1.assertValues(project.friends()[0].avatar().small())
        friendAvatarUrl2.assertValues(project.friends()[1].avatar().small())
        friendAvatarUrl3.assertNoValues()
        friendAvatar2IsHidden.assertValue(false)
        friendAvatar3IsHidden.assertValue(true)
    }

    @Test
    fun testFriendAvatarUrl_withThreeFriends() {
        val project = project()
            .toBuilder()
            .friends(Arrays.asList(user(), user(), user()))
            .build()
        setUpEnvironment()

        vm.inputs.configureWith(Pair.create(project, builder().build()))

        friendAvatarUrl1.assertValues(project.friends()[0].avatar().small())
        friendAvatarUrl2.assertValues(project.friends()[1].avatar().small())
        friendAvatarUrl3.assertValues(project.friends()[2].avatar().small())
        friendAvatar2IsHidden.assertValue(false)
        friendAvatar3IsHidden.assertValue(false)
    }

    @Test
    fun testFriendBackingViewIsNotHidden() {
        val project = project()
            .toBuilder()
            .friends(listOf(user()))
            .build()
        setUpEnvironment()

        vm.inputs.configureWith(Pair.create(project, builder().build()))

        // friends view is not hidden for project with friend backings
        friendBackingViewIsHidden.assertValues(false)
    }

    @Test
    fun testEmitsFriendBackingViewIsHidden() {
        val project = project().toBuilder().friends(null).build()
        setUpEnvironment()

        vm.inputs.configureWith(Pair.create(project, builder().build()))

        friendBackingViewIsHidden.assertValues(true)
    }

    @Test
    fun testFriendsForNamepile() {
        val project = project()
            .toBuilder()
            .friends(listOf(user()))
            .build()
        setUpEnvironment()

        vm.inputs.configureWith(Pair.create(project, builder().build()))

        friendsForNamepile.assertValues(project.friends())
    }

    @Test
    fun testFundingUnsuccessfulTextViewIsGone_projectLive() {
        val project = project().toBuilder().state(Project.STATE_LIVE).build()
        setUpEnvironment()

        vm.inputs.configureWith(Pair.create(project, builder().build()))

        fundingUnsuccessfulViewGroupIsGone.assertValues(true)
    }

    @Test
    fun testFundingUnsuccessfulViewGroupIsGone_projectFailed() {
        val project = project().toBuilder().state(Project.STATE_FAILED).build()
        setUpEnvironment()

        vm.inputs.configureWith(Pair.create(project, builder().build()))
        fundingUnsuccessfulViewGroupIsGone.assertValues(false)
    }

    @Test
    fun testFundingSuccessfulViewGroupIsGone_projectFailed() {
        val project = project().toBuilder().state(Project.STATE_FAILED).build()
        setUpEnvironment()
        vm.inputs.configureWith(Pair.create(project, builder().build()))
        fundingSuccessfulViewGroupIsGone.assertValues(true)
    }

    @Test
    fun testFundingSuccessfulViewGroupIsGone_projectSuccessful() {
        val project = project().toBuilder().state(Project.STATE_SUCCESSFUL).build()
        setUpEnvironment()
        vm.inputs.configureWith(Pair.create(project, builder().build()))
        fundingSuccessfulViewGroupIsGone.assertValues(false)
    }

    @Test
    fun testEmitsImageIsInvisible() {
        val project = project()
        setUpEnvironment()
        vm.inputs.configureWith(Pair.create(project, builder().build()))
        imageIsInvisible.assertValues(project.photo().isNull())
    }

    @Test
    fun testLocationContainerIsGone_whenSortIsDistance() {
        val project = project()
        setUpEnvironment()
        val discoveryParams = builder()
            .tagId(557)
            .build()
        vm.inputs.configureWith(Pair.create(project, discoveryParams))
        locationContainerIsGone.assertValues(false)
    }

    @Test
    fun testLocationContainerIsGone_whenSortIsNotDistance() {
        val project = project()
        setUpEnvironment()
        val discoveryParams = builder()
            .sort(DiscoveryParams.Sort.MAGIC)
            .build()
        vm.inputs.configureWith(Pair.create(project, discoveryParams))
        locationContainerIsGone.assertValues(true)
    }

    @Test
    fun testLocationName_whenLocationIsNull() {
        val project = project()
            .toBuilder()
            .location(null)
            .build()
        setUpEnvironment()
        vm.inputs.configureWith(Pair.create(project, builder().build()))
        locationName.assertNoValues()
    }

    @Test
    fun testLocationName_whenLocationIsNotNull() {
        val project = project()
        setUpEnvironment()
        vm.inputs.configureWith(Pair.create(project, builder().build()))
        locationName.assertValue("Brooklyn, NY")
    }

    @Test
    fun testMetadataViewGroupBackgroundColor() {
        val project = project().toBuilder().isBacking(true).build()
        setUpEnvironment()
        vm.inputs.configureWith(Pair.create(project, builder().build()))
        metadataViewGroupBackgroundDrawable.assertValues(R.drawable.rect_green_grey_stroke)
    }

    @Test
    fun testEmitsMetadataViewGroupIsGone() {
        val project = project().toBuilder().isStarred(true).build()
        setUpEnvironment()
        vm.inputs.configureWith(Pair.create(project, builder().build()))
        metadataViewGroupIsGone.assertValues(false)
    }

    @Test
    fun testEmitsNameAndBlurbText() {
        val nameAndBlurbPair = Pair.create("Farquaad", "Somebody once told me")
        val project =
            project().toBuilder().name(nameAndBlurbPair.first).blurb(nameAndBlurbPair.second)
                .build()
        setUpEnvironment()
        vm.inputs.configureWith(Pair.create(project, builder().build()))
        nameAndBlurbText.assertValues(nameAndBlurbPair)
    }

    @Test
    fun testNotifyDelegateOfProjectNameClick() {
        val project = project()
        setUpEnvironment()
        vm.inputs.configureWith(Pair.create(project, builder().build()))
        vm.inputs.projectCardClicked()
        notifyDelegateOfProjectClick.assertValues(project)
    }

    @Test
    fun testPercentageFunded_projectSuccessful() {
        val project = project().toBuilder().state(Project.STATE_SUCCESSFUL).build()
        setUpEnvironment()
        vm.inputs.configureWith(Pair.create(project, builder().build()))
        percentageFundedForProgressBar.assertValues(ProgressBarUtils.progress(project.percentageFunded()))
    }

    @Test
    fun testPercentageFunded_projectFailed() {
        val project = project().toBuilder().state(Project.STATE_FAILED).build()
        setUpEnvironment()
        vm.inputs.configureWith(Pair.create(project, builder().build()))

        percentageFundedForProgressBar.assertValues(ProgressBarUtils.progress(0.0f))
    }

    @Test
    fun testPercentageFundedTextViewText() {
        val project = project()
        setUpEnvironment()
        vm.inputs.configureWith(Pair.create(project, builder().build()))
        percentageFundedTextViewText.assertValues(NumberUtils.flooredPercentage(project.percentageFunded()))
    }

    @Test
    fun testEmitsPhotoUrl() {
        val project = project()
        setUpEnvironment()

        vm.inputs.configureWith(Pair.create(project, builder().build()))

        photoUrl.assertValues(project.photo()?.full())
    }

    @Test
    fun testProjectCanceledAt() {
        val project = project()
            .toBuilder()
            .state(Project.STATE_CANCELED)
            .stateChangedAt(DateTime.now())
            .build()
        setUpEnvironment()

        vm.inputs.configureWith(Pair.create(project, builder().build()))

        projectCanceledAt.assertValues(project.stateChangedAt())
    }

    @Test
    fun testProjectCardStatsViewGroupIsGone_isLive() {
        val project = project().toBuilder().state(Project.STATE_LIVE).build()
        setUpEnvironment()

        vm.inputs.configureWith(Pair.create(project, builder().build()))

        projectCardStatsViewGroupIsGone.assertValues(false)
    }

    @Test
    fun testProjectCardStatsViewGroupIsGone_isCanceled() {
        val project = project().toBuilder().state(Project.STATE_CANCELED).build()
        setUpEnvironment()

        vm.inputs.configureWith(Pair.create(project, builder().build()))

        projectCardStatsViewGroupIsGone.assertValues(true)
    }

    @Test
    fun testProjectFailedAt() {
        val project = project()
            .toBuilder()
            .state(Project.STATE_FAILED)
            .stateChangedAt(DateTime.now())
            .build()
        setUpEnvironment()

        vm.inputs.configureWith(Pair.create(project, builder().build()))

        projectFailedAt.assertValues(project.stateChangedAt())
    }

    @Test
    fun testProjectStateViewGroupIsGone_projectLive() {
        val project = project().toBuilder().state(Project.STATE_LIVE).build()
        setUpEnvironment()

        vm.inputs.configureWith(Pair.create(project, builder().build()))

        projectStateViewGroupIsGone.assertValues(true)
    }

    @Test
    fun testProjectStateViewGroupIsGone_projectSuccessful() {
        val project = project().toBuilder().state(Project.STATE_SUCCESSFUL).build()
        setUpEnvironment()

        vm.inputs.configureWith(Pair.create(project, builder().build()))

        projectStateViewGroupIsGone.assertValues(false)
    }

    @Test
    fun testProjectSubcategoryIsGone() {
        setUpEnvironment()
        val artProject = project().toBuilder().category(artCategory()).build()
        val ceramicsProject = project().toBuilder().category(ceramicsCategory()).build()
        val allProjects = builder().category(category()).build()
        val artProjects = builder().category(artCategory()).build()
        val ceramicsProjects = builder().category(ceramicsCategory()).build()

        // Root category is shown for project without subcategory when viewing all projects.
        vm.inputs.configureWith(Pair.create(artProject, allProjects))
        projectSubcategoryIsGone.assertValue(false)

        // Subcategory is shown when viewing all projects.
        vm.inputs.configureWith(Pair.create(ceramicsProject, allProjects))
        projectSubcategoryIsGone.assertValue(false)
        vm.inputs.configureWith(Pair.create(ceramicsProject, artProjects))
        projectSubcategoryIsGone.assertValue(false)
        vm.inputs.configureWith(Pair.create(ceramicsProject, ceramicsProjects))
        projectSubcategoryIsGone.assertValues(false, true)
        vm.inputs.configureWith(Pair.create(ceramicsProject, artProjects))
        projectSubcategoryIsGone.assertValues(false, true, false)
        vm.inputs.configureWith(Pair.create(artProject, artProjects))
        projectSubcategoryIsGone.assertValues(false, true, false, true)
    }

    @Test
    fun testProjectSubcategoryName() {
        val category = ceramicsCategory()
        val project = project().toBuilder().category(category).build()
        setUpEnvironment()

        vm.inputs.configureWith(Pair.create(project, builder().build()))

        projectSubcategoryName.assertValues(category.name())
    }

    @Test
    fun testProjectSuccessfulAt() {
        val project = project()
            .toBuilder()
            .state(Project.STATE_SUCCESSFUL)
            .stateChangedAt(DateTime.now())
            .build()
        setUpEnvironment()

        vm.inputs.configureWith(Pair.create(project, builder().build()))

        projectSuccessfulAt.assertValues(project.stateChangedAt())
    }

    @Test
    fun testProjectSuspendedAt() {
        val project = project()
            .toBuilder()
            .state(Project.STATE_SUSPENDED)
            .stateChangedAt(DateTime.now())
            .build()
        setUpEnvironment()

        vm.inputs.configureWith(Pair.create(project, builder().build()))

        projectSuspendedAt.assertValues(project.stateChangedAt())
    }

    @Test
    fun testProjectTagContainerIsGone() {
        setUpEnvironment()
        val artProject = project().toBuilder().category(artCategory()).build()
        val ceramicsProject = project().toBuilder().category(ceramicsCategory()).build()
        val ceramicsStaffPickProject = staffPick().toBuilder().category(ceramicsCategory()).build()
        val artStaffPickProject = staffPick().toBuilder().category(artCategory()).build()
        val allProjects = builder().category(category()).build()
        val artProjects = builder().category(artCategory()).build()
        val staffPicks = builder().staffPicks(true).build()
        val ceramicsProjects = builder().category(ceramicsCategory()).build()

        vm.inputs.configureWith(Pair.create(artProject, allProjects))
        projectTagContainerIsGone.assertValue(false)

        vm.inputs.configureWith(Pair.create(artStaffPickProject, allProjects))
        projectTagContainerIsGone.assertValue(false)

        vm.inputs.configureWith(Pair.create(artProject, artProjects))
        projectTagContainerIsGone.assertValues(false, true)

        vm.inputs.configureWith(Pair.create(artStaffPickProject, artProjects))
        projectTagContainerIsGone.assertValues(false, true, false)

        vm.inputs.configureWith(Pair.create(ceramicsProject, artProjects))
        projectTagContainerIsGone.assertValues(false, true, false)

        vm.inputs.configureWith(Pair.create(ceramicsStaffPickProject, artProjects))
        projectTagContainerIsGone.assertValues(false, true, false)

        vm.inputs.configureWith(Pair.create(ceramicsStaffPickProject, ceramicsProjects))
        projectTagContainerIsGone.assertValues(false, true, false)

        vm.inputs.configureWith(Pair.create(ceramicsProject, ceramicsProjects))
        projectTagContainerIsGone.assertValues(false, true, false, true)

        vm.inputs.configureWith(Pair.create(ceramicsProject, staffPicks))
        projectTagContainerIsGone.assertValues(false, true, false, true)

        vm.inputs.configureWith(Pair.create(ceramicsStaffPickProject, staffPicks))
        projectTagContainerIsGone.assertValues(false, true, false, true)

        vm.inputs.configureWith(Pair.create(artProject, staffPicks))
        projectTagContainerIsGone.assertValues(false, true, false, true, false)

        vm.inputs.configureWith(Pair.create(artStaffPickProject, staffPicks))
        projectTagContainerIsGone.assertValues(false, true, false, true, false)
    }

    @Test
    fun testProjectWeLoveIsGone() {
        setUpEnvironment()
        val musicProject = project()
        val staffPickProject = staffPick()
        val allProjects = builder().build()
        val staffPicks = builder().staffPicks(true).build()

        vm.inputs.configureWith(Pair.create(musicProject, allProjects))
        projectWeLoveIsGone.assertValue(true)

        vm.inputs.configureWith(Pair.create(staffPickProject, allProjects))
        projectWeLoveIsGone.assertValues(true, false)

        vm.inputs.configureWith(Pair.create(staffPickProject, staffPicks))
        projectWeLoveIsGone.assertValues(true, false, true)
    }

    @Test
    fun testRootCategoryNameForFeatured() {
        val category = bluesCategory()
        val project = project()
            .toBuilder()
            .category(category)
            .build()
        setUpEnvironment()

        vm.inputs.configureWith(Pair.create(project, builder().build()))

        rootCategoryNameForFeatured.assertValues(category.root()?.name())
    }

    @Test
    fun testSetDefaultTopPadding_noMetaData() {
        val project = project()
            .toBuilder()
            .isBacking(false)
            .isStarred(false)
            .featuredAt(null)
            .build()
        setUpEnvironment()

        vm.inputs.configureWith(Pair.create(project, builder().build()))

        setDefaultTopPadding.assertValue(true)
    }

    @Test
    fun testSetDefaultTopPadding_withMetaData() {
        val project = project()
            .toBuilder()
            .isBacking(true)
            .isStarred(false)
            .featuredAt(null)
            .build()
        setUpEnvironment()

        vm.inputs.configureWith(Pair.create(project, builder().build()))

        setDefaultTopPadding.assertValue(false)
    }

    @Test
    fun testStarredViewGroupIsGone_isStarred() {
        val project = project().toBuilder().isStarred(true).build()
        setUpEnvironment()

        vm.inputs.configureWith(Pair.create(project, builder().build()))

        savedViewGroupIsGone.assertValues(false)
    }

    @Test
    fun testStarredViewGroupIsGone_isStarred_isBacking() {
        val project = project().toBuilder().isBacking(true).isStarred(true).build()
        setUpEnvironment()

        vm.inputs.configureWith(Pair.create(project, builder().build()))

        savedViewGroupIsGone.assertValues(true)
    }

    @Test
    fun testComingSoonViewGroupIsGone_isPreLaunch() {
        val project = project().toBuilder().displayPrelaunch(true).isStarred(false).build()
        setUpEnvironment()

        vm.inputs.configureWith(Pair.create(project, builder().build()))

        savedViewGroupIsGone.assertValues(true)
        backingViewGroupIsGone.assertValues(true)
        comingSoonViewGroupIsGone.assertValues(false)
    }

    @Test
    fun testComingSoonViewGroupIsGone_isPreLaunch_isStarred() {
        val project = project().toBuilder().displayPrelaunch(true).isStarred(true).build()
        setUpEnvironment()

        vm.inputs.configureWith(Pair.create(project, builder().build()))

        savedViewGroupIsGone.assertValues(true)
        backingViewGroupIsGone.assertValues(true)
        comingSoonViewGroupIsGone.assertValues(false)
    }

    @Test
    fun testComingSoonViewGroupIsGone_isPreLaunch_isStarred_isBacking() {
        val project =
            project().toBuilder().isBacking(true).displayPrelaunch(true).isStarred(true).build()
        setUpEnvironment()

        vm.inputs.configureWith(Pair.create(project, builder().build()))

        savedViewGroupIsGone.assertValues(true)
        backingViewGroupIsGone.assertValues(false)
        comingSoonViewGroupIsGone.assertValues(true)
    }

    @After
    fun clear() {
        disposables.clear()
    }
}
