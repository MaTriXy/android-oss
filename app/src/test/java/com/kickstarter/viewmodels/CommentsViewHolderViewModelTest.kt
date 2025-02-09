package com.kickstarter.viewmodels

import com.kickstarter.KSRobolectricTestCase
import com.kickstarter.libs.Environment
import com.kickstarter.libs.MockCurrentUserV2
import com.kickstarter.libs.utils.extensions.addToDisposable
import com.kickstarter.mock.factories.AvatarFactory
import com.kickstarter.mock.factories.CommentFactory
import com.kickstarter.mock.factories.ProjectFactory
import com.kickstarter.mock.factories.UserFactory
import com.kickstarter.mock.services.MockApolloClientV2
import com.kickstarter.models.Comment
import com.kickstarter.services.mutations.PostCommentData
import com.kickstarter.type.CommentBadge
import com.kickstarter.ui.data.CommentCardData
import com.kickstarter.ui.views.CommentCardBadge
import com.kickstarter.ui.views.CommentCardStatus
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.TestScheduler
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subscribers.TestSubscriber
import org.joda.time.DateTime
import org.junit.After
import org.junit.Test
import java.util.concurrent.TimeUnit

class CommentsViewHolderViewModelTest : KSRobolectricTestCase() {

    private lateinit var vm: CommentsViewHolderViewModel.ViewModel

    private val commentCardStatus = TestSubscriber<CommentCardStatus>()
    private val commentAuthorName = TestSubscriber<String>()
    private val commentAuthorAvatarUrl = TestSubscriber<String>()
    private val commentMessageBody = TestSubscriber<String>()
    private val commentPostTime = TestSubscriber<DateTime>()
    private val isReplyButtonVisible = TestSubscriber<Boolean>()
    private val openCommentGuideLines = TestSubscriber<Comment>()
    private val retrySendComment = TestSubscriber<Comment>()
    private val replyToComment = TestSubscriber<Comment>()
    private val flagComment = TestSubscriber<Comment>()
    private val repliesCount = TestSubscriber<Int>()
    private val commentSuccessfullyPosted = TestSubscriber<Comment>()
    private val testScheduler = TestScheduler()
    private val isCommentReply = TestSubscriber<Unit>()
    private val authorBadge = TestSubscriber<CommentCardBadge?>()

    private val createdAt = DateTime.now()
    private val currentUser = UserFactory.user().toBuilder().id(1).avatar(
        AvatarFactory.avatar()
    ).name("joe").build()

    private val disposables = CompositeDisposable()

    @After
    fun cleanUp() {
        disposables.clear()
    }

    private fun setUpEnvironment(environment: Environment) {
        this.vm = CommentsViewHolderViewModel.ViewModel(environment)

        this.vm.outputs.commentCardStatus().subscribe { this.commentCardStatus.onNext(it) }.addToDisposable(disposables)
        this.vm.outputs.commentAuthorName().subscribe { this.commentAuthorName.onNext(it) }.addToDisposable(disposables)
        this.vm.outputs.commentAuthorAvatarUrl().subscribe { this.commentAuthorAvatarUrl.onNext(it) }.addToDisposable(disposables)
        this.vm.outputs.commentMessageBody().subscribe { this.commentMessageBody.onNext(it) }.addToDisposable(disposables)
        this.vm.outputs.commentPostTime().subscribe { this.commentPostTime.onNext(it) }.addToDisposable(disposables)
        this.vm.outputs.isReplyButtonVisible().subscribe { this.isReplyButtonVisible.onNext(it) }.addToDisposable(disposables)
        this.vm.outputs.openCommentGuideLines().subscribe { this.openCommentGuideLines.onNext(it) }.addToDisposable(disposables)
        this.vm.outputs.retrySendComment().subscribe { this.retrySendComment.onNext(it) }.addToDisposable(disposables)
        this.vm.outputs.replyToComment().subscribe { this.replyToComment.onNext(it) }.addToDisposable(disposables)
        this.vm.outputs.flagComment().subscribe { this.flagComment.onNext(it) }.addToDisposable(disposables)
        this.vm.outputs.commentRepliesCount().subscribe { this.repliesCount.onNext(it) }.addToDisposable(disposables)
        this.vm.outputs.isSuccessfullyPosted().subscribe { this.commentSuccessfullyPosted.onNext(it) }.addToDisposable(disposables)
        this.vm.isCommentReply().subscribe { this.isCommentReply.onNext(it) }.addToDisposable(disposables)
        this.vm.outputs.authorBadge().subscribe { this.authorBadge.onNext(it) }.addToDisposable(disposables)
    }

    @Test
    fun testOpenCommentGuideLinesClicked() {
        setUpEnvironment(environment())
        val commentCardData = CommentFactory.liveCommentCardData(createdAt = createdAt, currentUser = currentUser)

        this.vm.inputs.configureWith(commentCardData)
        this.vm.inputs.onCommentGuideLinesClicked()

        this.openCommentGuideLines.assertValue(commentCardData.comment)
    }

    @Test
    fun testReplyToCommentClicked() {
        setUpEnvironment(environment())
        val commentCardData = CommentFactory.liveCommentCardData(createdAt = createdAt, currentUser = currentUser)

        this.vm.inputs.configureWith(commentCardData)
        this.vm.inputs.onReplyButtonClicked()

        this.replyToComment.assertValue(commentCardData.comment)
    }

    @Test
    fun testRetrySendCommentClicked() {
        setUpEnvironment(environment())
        val commentCardData = CommentFactory.liveCommentCardData(createdAt = createdAt, currentUser = currentUser)

        this.vm.inputs.configureWith(commentCardData)
        this.vm.inputs.onRetryViewClicked()

        this.retrySendComment.assertValue(commentCardData.comment)
    }

    @Test
    fun testFlagCommentClicked() {
        setUpEnvironment(environment())
        val commentCardData = CommentFactory.liveCommentCardData(createdAt = createdAt, currentUser = currentUser)
        this.vm.inputs.configureWith(commentCardData)
        this.vm.inputs.onFlagButtonClicked()

        this.flagComment.assertValue(commentCardData.comment)
    }

    @Test
    fun testUserAvatarUrl() {
        setUpEnvironment(environment())
        val userAvatar = AvatarFactory.avatar()
        val currentUser = UserFactory.user().toBuilder().id(111).avatar(
            userAvatar
        ).build()
        val commentCardData = CommentFactory.liveCommentCardData(createdAt = createdAt, currentUser = currentUser)
        this.vm.inputs.configureWith(commentCardData)

        this.commentAuthorAvatarUrl.assertValue(userAvatar.medium())
        this.commentCardStatus.assertValue(CommentCardStatus.COMMENT_FOR_LOGIN_BACKED_USERS)
    }

    @Test
    fun testUserAvatarUrl_whenMediumIsEmpty_TrySmall() {
        setUpEnvironment(environment())
        val userAvatar = AvatarFactory.avatar().toBuilder()
            .medium("")
            .build()
        val currentUser = UserFactory.user().toBuilder().id(111).avatar(
            userAvatar
        ).build()
        val commentCardData = CommentFactory.liveCommentCardData(createdAt = createdAt, currentUser = currentUser)
        this.vm.inputs.configureWith(commentCardData)

        this.commentAuthorAvatarUrl.assertValue(userAvatar.small())
        this.commentCardStatus.assertValue(CommentCardStatus.COMMENT_FOR_LOGIN_BACKED_USERS)
    }

    @Test
    fun testUserAvatarUrl_whenMediumAndSmallEmpty_Empty() {
        setUpEnvironment(environment())
        val userAvatar = AvatarFactory.avatar().toBuilder()
            .medium("")
            .small("")
            .build()
        val currentUser = UserFactory.user().toBuilder().id(111).avatar(
            userAvatar
        ).build()
        val commentCardData = CommentFactory.liveCommentCardData(createdAt = createdAt, currentUser = currentUser)
        this.vm.inputs.configureWith(commentCardData)

        this.commentAuthorAvatarUrl.assertValue("")
        this.commentCardStatus.assertValue(CommentCardStatus.COMMENT_FOR_LOGIN_BACKED_USERS)
    }

    @Test
    fun testCommentAuthorName() {
        setUpEnvironment(environment())
        val commentCardData = CommentFactory.liveCommentCardData(createdAt = createdAt, currentUser = currentUser)

        this.vm.inputs.configureWith(commentCardData)

        this.commentAuthorName.assertValue(commentCardData.comment?.author()?.name())
        this.commentCardStatus.assertValue(CommentCardStatus.COMMENT_FOR_LOGIN_BACKED_USERS)
    }

    @Test
    fun testCommentMessageBody() {
        setUpEnvironment(environment())
        val commentCardData = CommentFactory.liveCommentCardData(createdAt = createdAt, currentUser = currentUser)

        this.vm.inputs.configureWith(commentCardData)

        this.commentMessageBody.assertValue(commentCardData.comment?.body())
        this.commentCardStatus.assertValue(CommentCardStatus.COMMENT_FOR_LOGIN_BACKED_USERS)
    }

    @Test
    fun testDeletedComment() {
        setUpEnvironment(environment())
        val commentCardData = CommentFactory.liveCommentCardData(createdAt = createdAt, currentUser = currentUser, isDelete = true)

        this.vm.inputs.configureWith(commentCardData)

        this.commentCardStatus.assertValue(CommentCardStatus.DELETED_COMMENT)
    }

    @Test
    fun testCommentPostTime() {
        setUpEnvironment(environment())
        val commentCardData = CommentFactory.liveCommentCardData(createdAt = createdAt, currentUser = currentUser, isDelete = true)

        this.vm.inputs.configureWith(commentCardData)

        this.commentPostTime.assertValue(commentCardData.comment?.createdAt())
    }

    @Test
    fun testNoReplyCountForBindingCardStatus() {
        setUpEnvironment(environment())
        val commentCardData = CommentFactory.liveCommentCardData(createdAt = createdAt, currentUser = currentUser)

        this.vm.inputs.configureWith(commentCardData)

        this.commentCardStatus.assertValue(CommentCardStatus.COMMENT_FOR_LOGIN_BACKED_USERS)
    }

    @Test
    fun testReplyCountForBindingCardStatus() {
        setUpEnvironment(environment())

        val commentCardData = CommentFactory.liveCommentCardData(createdAt = createdAt, currentUser = currentUser, repliesCount = 20)
        this.vm.inputs.configureWith(commentCardData)
        this.commentCardStatus.assertValue(CommentCardStatus.COMMENT_WITH_REPLIES)
    }

    @Test
    fun testCommentReplyButtonVisibility_whenUserLoggedInAndProjectBacked_shouldSendTrue() {
        val environment = environment().toBuilder()
            .currentUserV2(MockCurrentUserV2(UserFactory.user()))
            .build()
        setUpEnvironment(environment)
        val comment = CommentFactory.comment()
        val commentCardData = CommentCardData.builder().comment(comment).project(ProjectFactory.backedProject()).build()
        this.vm.inputs.configureWith(commentCardData)
        this.isReplyButtonVisible.assertValue(true)
    }

    @Test
    fun testCommentReplyButtonVisibility_whenUserLoggedInAndProjectNotBacked_shouldSendFalse() {
        val environment = environment().toBuilder()
            .currentUserV2(MockCurrentUserV2(UserFactory.user()))
            .build()
        setUpEnvironment(environment)
        val comment = CommentFactory.comment()
        val commentCardData = CommentCardData.builder().comment(comment).project(ProjectFactory.project()).build()
        this.vm.inputs.configureWith(commentCardData)
        this.isReplyButtonVisible.assertValue(false)
    }

    @Test
    fun testCommentReplyButtonVisibility_whenProjectNotBackedAndUserIsCreator_shouldSendTrue() {
        val user = UserFactory.creator().toBuilder().id(2).build()

        val environment = environment().toBuilder()
            .currentUserV2(MockCurrentUserV2(user))
            .build()
        setUpEnvironment(environment)

        val comment = CommentFactory.comment()
        val commentCardData = CommentCardData.builder().comment(comment).project(ProjectFactory.project().toBuilder().creator(user).build()).build()
        this.vm.inputs.configureWith(commentCardData)
        this.isReplyButtonVisible.assertValue(true)
    }

    @Test
    fun testCommentReplyButtonVisibility_whenUserNotLoggedInFFOn_shouldSendFalse() {
        val environment = environment().toBuilder()
            .build()
        setUpEnvironment(environment)

        val comment = CommentFactory.comment()
        val commentCardData = CommentCardData.builder().comment(comment).project(ProjectFactory.project()).build()
        this.vm.inputs.configureWith(commentCardData)
        this.isReplyButtonVisible.assertValue(false)
    }

    @Test
    fun testSetRepliesCount() {
        val repliesCount = BehaviorSubject.create<Int>()

        val environment = environment().toBuilder()
            .currentUserV2(MockCurrentUserV2(UserFactory.user()))
            .build()
        setUpEnvironment(environment)
        val comment = CommentFactory.comment(repliesCount = 1)
        val commentData = CommentCardData.builder().comment(comment).project(ProjectFactory.project()).build()
        this.vm.outputs.commentRepliesCount().subscribe(repliesCount)
        this.vm.inputs.configureWith(commentData)
        assertEquals(comment.repliesCount(), repliesCount.value)
    }

    @Test
    fun testRetrySendCommentErrorClicked() {
        val currentUser = UserFactory.user().toBuilder().id(1).build()
        val env = environment().toBuilder().apolloClientV2(object : MockApolloClientV2() {
            override fun createComment(comment: PostCommentData): Observable<Comment> {
                return Observable.error(Throwable())
            }
        })
            .schedulerV2(testScheduler)
            .currentUserV2(MockCurrentUserV2(currentUser))
            .build()

        setUpEnvironment(env)

        val comment = CommentFactory.commentToPostWithUser(currentUser)
        val commentCardData = CommentCardData.builder()
            .comment(comment)
            .project(ProjectFactory.initialProject())
            .commentableId(ProjectFactory.initialProject().id().toString())
            .commentCardState(CommentCardStatus.TRYING_TO_POST.commentCardStatus)
            .build()

        this.vm.inputs.configureWith(commentCardData)

        testScheduler.advanceTimeBy(2, TimeUnit.SECONDS)

        this.vm.inputs.onRetryViewClicked()

        this.retrySendComment.assertValue(comment)

        testScheduler.advanceTimeBy(2, TimeUnit.SECONDS)

        this.retrySendComment.assertValue(comment)

        this.commentCardStatus.assertValues(
            CommentCardStatus.TRYING_TO_POST,
            CommentCardStatus.FAILED_TO_SEND_COMMENT,
            CommentCardStatus.RE_TRYING_TO_POST,
            CommentCardStatus.FAILED_TO_SEND_COMMENT,
        )

        this.vm.inputs.onRetryViewClicked()

        testScheduler.advanceTimeBy(2, TimeUnit.SECONDS)

        this.commentCardStatus.assertValues(
            CommentCardStatus.TRYING_TO_POST,
            CommentCardStatus.FAILED_TO_SEND_COMMENT,
            CommentCardStatus.RE_TRYING_TO_POST,
            CommentCardStatus.FAILED_TO_SEND_COMMENT,
            CommentCardStatus.RE_TRYING_TO_POST,
            CommentCardStatus.FAILED_TO_SEND_COMMENT
        )

        this.commentSuccessfullyPosted.assertNoValues()
    }

    @Test
    fun testRetrySendComment_whenReplyAndAfterSuccess_shouldSendReplyButtonVisibilityFalse() {
        val currentUser = UserFactory.user().toBuilder().id(1).build()
        val reply = CommentFactory.reply(createdAt = createdAt)
        val commentCardData = CommentCardData.builder()
            .comment(reply)
            .project(ProjectFactory.initialProject())
            .commentableId(ProjectFactory.initialProject().id().toString())
            .commentCardState(CommentCardStatus.TRYING_TO_POST.commentCardStatus)
            .build()

        var isFirstRun = true

        var env = environment().toBuilder().apolloClientV2(object : MockApolloClientV2() {
            override fun createComment(comment: PostCommentData): Observable<Comment> {
                return if (isFirstRun) Observable.error(Throwable()) else Observable.just(reply)
            }
        })
            .schedulerV2(testScheduler)
            .currentUserV2(MockCurrentUserV2(currentUser))
            .build()

        setUpEnvironment(env)

        this.vm.inputs.configureWith(commentCardData)

        testScheduler.advanceTimeBy(2, TimeUnit.SECONDS)

        this.vm.inputs.onRetryViewClicked()

        testScheduler.advanceTimeBy(2, TimeUnit.SECONDS)

        this.commentCardStatus.assertValues(
            CommentCardStatus.TRYING_TO_POST,
            CommentCardStatus.FAILED_TO_SEND_COMMENT,
            CommentCardStatus.RE_TRYING_TO_POST,
            CommentCardStatus.FAILED_TO_SEND_COMMENT,
        )

        this.commentSuccessfullyPosted.assertNoValues()

        isFirstRun = false

        this.vm.inputs.onRetryViewClicked()

        testScheduler.advanceTimeBy(4, TimeUnit.SECONDS)

        this.commentCardStatus.assertValues(
            CommentCardStatus.TRYING_TO_POST,
            CommentCardStatus.FAILED_TO_SEND_COMMENT,
            CommentCardStatus.RE_TRYING_TO_POST,
            CommentCardStatus.FAILED_TO_SEND_COMMENT,
            CommentCardStatus.RE_TRYING_TO_POST,
            CommentCardStatus.POSTING_COMMENT_COMPLETED_SUCCESSFULLY,
            CommentCardStatus.COMMENT_FOR_LOGIN_BACKED_USERS
        )

        this.isCommentReply.assertValues(Unit, Unit)
        this.commentSuccessfullyPosted.assertValues(reply)
        this.isReplyButtonVisible.assertValues(false, false)
    }

    @Test
    fun testSendCommentShouldNotPost() {
        val responseComment = CommentFactory.liveComment(createdAt = createdAt)
        val currentUser = UserFactory.user().toBuilder().id(1).build()
        val env = environment().toBuilder()
            .apolloClientV2(object : MockApolloClientV2() {
                override fun createComment(comment: PostCommentData): Observable<Comment> {
                    return Observable.just(responseComment)
                }
            })
            .currentUserV2(MockCurrentUserV2(currentUser))
            .build()
        setUpEnvironment(env)

        val comment = CommentFactory.commentToPostWithUser(currentUser)
        val commentCardData = CommentCardData.builder()
            .comment(comment)
            .project(ProjectFactory.initialProject())
            .commentCardState(CommentCardStatus.COMMENT_FOR_LOGIN_BACKED_USERS.commentCardStatus)
            .build()

        this.vm.inputs.configureWith(commentCardData)

        this.commentCardStatus.assertValue(
            CommentCardStatus.COMMENT_FOR_LOGIN_BACKED_USERS
        )

        this.commentSuccessfullyPosted.assertNoValues()
    }

    @Test
    fun testSendCommentClicked() {
        val commentResponse = CommentFactory.liveComment(createdAt = createdAt)
        val currentUser = UserFactory.user().toBuilder().id(1).build()
        val env = environment().toBuilder()
            .apolloClientV2(object : MockApolloClientV2() {
                override fun createComment(comment: PostCommentData): Observable<Comment> {
                    return Observable.just(commentResponse)
                }
            })
            .currentUserV2(MockCurrentUserV2(currentUser))
            .build()
        setUpEnvironment(env)

        val comment = CommentFactory.commentToPostWithUser(currentUser)
        val commentCardData = CommentCardData.builder()
            .comment(comment)
            .project(ProjectFactory.initialProject())
            .commentableId(ProjectFactory.initialProject().id().toString())
            .commentCardState(CommentCardStatus.TRYING_TO_POST.commentCardStatus)
            .build()

        this.vm.inputs.configureWith(commentCardData)

        this.commentCardStatus.assertValues(
            CommentCardStatus.TRYING_TO_POST,
            CommentCardStatus.COMMENT_FOR_LOGIN_BACKED_USERS
        )

        this.commentSuccessfullyPosted.assertValue(commentResponse)
    }

    @Test
    fun testSendCommentClicked_withOtherUser() {
        val currentUser = UserFactory.user().toBuilder().id(1).build()
        val env = environment().toBuilder()
            .apolloClientV2(object : MockApolloClientV2() {
                override fun createComment(comment: PostCommentData): Observable<Comment> {
                    return Observable.just(CommentFactory.liveComment(createdAt = createdAt))
                }
            })
            .currentUserV2(MockCurrentUserV2(currentUser))
            .build()
        setUpEnvironment(env)

        val comment = CommentFactory.commentToPostWithUser(UserFactory.germanUser())
        val commentCardData = CommentCardData.builder()
            .comment(comment)
            .project(ProjectFactory.initialProject())
            .commentCardState(CommentCardStatus.TRYING_TO_POST.commentCardStatus)
            .build()

        this.vm.inputs.configureWith(commentCardData)

        // State has not changed from the initialization
        this.commentCardStatus.assertValues(
            CommentCardStatus.TRYING_TO_POST
        )

        this.commentSuccessfullyPosted.assertNoValues()
    }

    @Test
    fun testSendCommentFailedAndPressRetry() {
        val currentUser = UserFactory.user().toBuilder().id(1).build()
        val env = environment().toBuilder()
            .apolloClientV2(object : MockApolloClientV2() {
                override fun createComment(comment: PostCommentData): Observable<Comment> {
                    return Observable.error(Throwable())
                }
            })
            .schedulerV2(testScheduler)
            .currentUserV2(MockCurrentUserV2(currentUser))
            .build()
        setUpEnvironment(env)

        val comment = CommentFactory.commentToPostWithUser(currentUser)
        val commentCardData = CommentCardData.builder()
            .comment(comment)
            .project(ProjectFactory.initialProject())
            .commentableId(ProjectFactory.initialProject().id().toString())
            .commentCardState(CommentCardStatus.TRYING_TO_POST.commentCardStatus)
            .build()

        this.vm.inputs.configureWith(commentCardData)
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        this.vm.inputs.onRetryViewClicked()
        testScheduler.advanceTimeBy(3, TimeUnit.SECONDS)

        this.retrySendComment.assertValue(comment)
        this.commentCardStatus.assertValues(
            CommentCardStatus.TRYING_TO_POST,
            CommentCardStatus.FAILED_TO_SEND_COMMENT,
            CommentCardStatus.RE_TRYING_TO_POST,
            CommentCardStatus.FAILED_TO_SEND_COMMENT
        )

        this.commentSuccessfullyPosted.assertNoValues()
    }

    @Test
    fun testIsCommentReply() {
        val reply = CommentFactory.reply(createdAt = createdAt)
        val commentCardData = CommentCardData.builder()
            .comment(reply)
            .project(ProjectFactory.initialProject())
            .commentCardState(CommentCardStatus.TRYING_TO_POST.commentCardStatus)
            .build()

        val env = environment()
        setUpEnvironment(env)

        this.vm.inputs.configureWith(commentCardData)

        // State has not changed from the initialization
        this.commentCardStatus.assertValues(
            CommentCardStatus.TRYING_TO_POST
        )

        this.isCommentReply.assertValue(Unit)
    }

    @Test
    fun testCommentsViewModel_whenCommentFlagged_shouldSetStatusToFlagged() {
        val env = environment().toBuilder().currentUserV2(MockCurrentUserV2(UserFactory.user())).build()
        setUpEnvironment(env)

        val commentCardData = CommentFactory.liveCommentCardData(createdAt = createdAt, currentUser = currentUser, hasFlaggings = true)

        this.vm.inputs.configureWith(commentCardData)

        this.commentCardStatus.assertValue(CommentCardStatus.FLAGGED_COMMENT)
    }

    @Test
    fun testCommentsViewModel_whenCommentFlaggedAndUserIsAuthor_shouldNotSetStatusToFlagged() {
        val user = UserFactory.user()
        val env = environment().toBuilder().currentUserV2(MockCurrentUserV2(user)).build()
        setUpEnvironment(env)

        val commentCardData = CommentFactory.liveCommentCardData(createdAt = createdAt, currentUser = user, hasFlaggings = true)

        this.vm.inputs.configureWith(commentCardData)

        this.commentCardStatus.assertValue(CommentCardStatus.COMMENT_FOR_LOGIN_BACKED_USERS)
    }

    @Test
    fun testCommentsViewModel_whenCommentFlaggedAndSustained_shouldNotSetStatusToFlagged() {

        val env = environment().toBuilder().currentUserV2(MockCurrentUserV2(UserFactory.user())).build()
        setUpEnvironment(env)

        val commentCardData = CommentFactory.liveCommentCardData(createdAt = createdAt, currentUser = currentUser, hasFlaggings = true, sustained = true)

        this.vm.inputs.configureWith(commentCardData)

        this.commentCardStatus.assertValue(CommentCardStatus.COMMENT_FOR_LOGIN_BACKED_USERS)
    }

    @Test
    fun testCommentsViewModel_whenCommentFlaggedAndDeleted_shouldNotSetStatusToFlagged() {
        val env = environment().toBuilder().currentUserV2(MockCurrentUserV2(UserFactory.user())).build()
        setUpEnvironment(env)

        val commentCardData = CommentFactory.liveCommentCardData(createdAt = createdAt, currentUser = currentUser, isDelete = true, hasFlaggings = true, sustained = false)

        this.vm.inputs.configureWith(commentCardData)

        this.commentCardStatus.assertValue(CommentCardStatus.DELETED_COMMENT)
    }

    @Test
    fun testPostReply_Successful() {
        val reply = CommentFactory.reply(createdAt = createdAt)
        val currentUser = UserFactory.user().toBuilder().id(1).build()

        val env = environment().toBuilder()
            .apolloClientV2(object : MockApolloClientV2() {
                override fun createComment(comment: PostCommentData): Observable<Comment> {
                    return Observable.just(reply)
                }
            })
            .currentUserV2(MockCurrentUserV2(currentUser))
            .build()
        setUpEnvironment(env)

        val comment = CommentFactory.commentToPostWithUser(currentUser)
        val commentCardData = CommentCardData.builder()
            .comment(comment)
            .project(ProjectFactory.initialProject())
            .commentableId(ProjectFactory.initialProject().id().toString())
            .commentCardState(CommentCardStatus.TRYING_TO_POST.commentCardStatus)
            .build()

        this.vm.inputs.configureWith(commentCardData)

        this.commentCardStatus.assertValues(
            CommentCardStatus.TRYING_TO_POST,
            CommentCardStatus.COMMENT_FOR_LOGIN_BACKED_USERS
        )

        this.commentSuccessfullyPosted.assertValue(reply)
    }

    @Test
    fun testAuthorCanceledPledgeComment() {
        setUpEnvironment(environment())
        val commentCardData = CommentFactory.liveCanceledPledgeCommentCardData(createdAt = createdAt, currentUser = currentUser)

        this.vm.inputs.configureWith(commentCardData)

        this.commentCardStatus.assertValue(CommentCardStatus.CANCELED_PLEDGE_MESSAGE)

        this.vm.inputs.configureWith(commentCardData.toBuilder().commentCardState(CommentCardStatus.CANCELED_PLEDGE_COMMENT.commentCardStatus).build())

        this.commentCardStatus.assertValues(CommentCardStatus.CANCELED_PLEDGE_MESSAGE, CommentCardStatus.CANCELED_PLEDGE_COMMENT)
    }

    @Test
    fun commentBadge_whenYouAndSuperbacker_shouldEmitSuperbacker() {
        val authorBadges = listOf<String>(CommentBadge.superbacker.rawValue)
        val author = UserFactory.user().toBuilder().id(1).build()
        val currentUser = UserFactory.user().toBuilder().id(1).build()
        val environment = environment().toBuilder()
            .currentUserV2(MockCurrentUserV2(currentUser))
            .build()
        setUpEnvironment(environment)

        val comment = CommentFactory.commentFromCurrentUser(author, authorBadges)
        val commentCardData = CommentCardData.builder()
            .comment(comment)
            .project(ProjectFactory.initialProject())
            .commentCardState(CommentCardStatus.COMMENT_FOR_LOGIN_BACKED_USERS.commentCardStatus)
            .build()

        this.vm.inputs.configureWith(commentCardData)

        this.authorBadge.assertValue(CommentCardBadge.SUPERBACKER)
    }

    @Test
    fun commentBadge_whenCreator_shouldEmitCreator() {
        val currentUser = UserFactory.user().toBuilder().id(1).build()
        val environment = environment().toBuilder()
            .currentUserV2(MockCurrentUserV2(currentUser))
            .build()

        setUpEnvironment(environment)
        val authorBadges = listOf<String>(CommentBadge.superbacker.rawValue, CommentBadge.creator.rawValue)
        val author = UserFactory.user().toBuilder().id(2).build()
        val comment = CommentFactory.commentFromCurrentUser(author, authorBadges)
        val commentCardData = CommentCardData.builder()
            .comment(comment)
            .project(ProjectFactory.initialProject())
            .commentCardState(CommentCardStatus.COMMENT_FOR_LOGIN_BACKED_USERS.commentCardStatus)
            .build()

        this.vm.inputs.configureWith(commentCardData)

        this.authorBadge.assertValue(CommentCardBadge.CREATOR)
    }

    @Test
    fun commentBadge_whenSuperBacker_shouldEmitSuperbacker() {
        val currentUser = UserFactory.user().toBuilder().id(1).build()
        val environment = environment().toBuilder()
            .currentUserV2(MockCurrentUserV2(currentUser))
            .build()

        setUpEnvironment(environment)
        val authorBadges = listOf<String>(CommentBadge.superbacker.rawValue)
        val author = UserFactory.user().toBuilder().id(2).build()
        val comment = CommentFactory.commentFromCurrentUser(author, authorBadges)
        val commentCardData = CommentCardData.builder()
            .comment(comment)
            .project(ProjectFactory.initialProject())
            .commentCardState(CommentCardStatus.COMMENT_FOR_LOGIN_BACKED_USERS.commentCardStatus)
            .build()

        this.vm.inputs.configureWith(commentCardData)

        this.authorBadge.assertValue(CommentCardBadge.SUPERBACKER)
    }

    @Test
    fun commentBadge_whenNoBadge_shouldEmitNoBadge() {
        val currentUser = UserFactory.user().toBuilder().id(1).build()
        val environment = environment().toBuilder()
            .currentUserV2(MockCurrentUserV2(currentUser))
            .build()

        setUpEnvironment(environment)
        val authorBadges = listOf<String>()
        val author = UserFactory.user().toBuilder().id(2).build()
        val comment = CommentFactory.commentFromCurrentUser(author, authorBadges)
        val commentCardData = CommentCardData.builder()
            .comment(comment)
            .project(ProjectFactory.initialProject())
            .commentCardState(CommentCardStatus.COMMENT_FOR_LOGIN_BACKED_USERS.commentCardStatus)
            .build()

        this.vm.inputs.configureWith(commentCardData)

        this.authorBadge.assertValue(CommentCardBadge.NO_BADGE)
    }

    @Test
    fun commentBadge_whenNotLoggedInAndCommentIsFromCreator_shouldEmitCreator() {
        setUpEnvironment(environment())

        val authorBadges = listOf<String>(CommentBadge.superbacker.rawValue, CommentBadge.creator.rawValue)
        val author = UserFactory.user().toBuilder().id(2).build()
        val comment = CommentFactory.commentFromCurrentUser(author, authorBadges)
        val commentCardData = CommentCardData.builder()
            .comment(comment)
            .project(ProjectFactory.initialProject())
            .commentCardState(CommentCardStatus.COMMENT_WITH_REPLIES.commentCardStatus)
            .build()

        this.vm.inputs.configureWith(commentCardData)

        this.authorBadge.assertValue(CommentCardBadge.CREATOR)
    }
}
