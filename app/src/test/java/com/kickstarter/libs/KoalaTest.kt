package com.kickstarter.libs

import com.kickstarter.KSRobolectricTestCase
import com.kickstarter.mock.MockCurrentConfig
import com.kickstarter.mock.MockExperimentsClientType
import com.kickstarter.mock.factories.*
import com.kickstarter.models.Project
import com.kickstarter.models.User
import com.kickstarter.services.DiscoveryParams
import org.joda.time.DateTime
import org.json.JSONArray
import org.junit.Test
import rx.subjects.BehaviorSubject

class KoalaTest : KSRobolectricTestCase() {

    private val propertiesTest = BehaviorSubject.create<Map<String, Any>>()

    @Test
    fun testDefaultProperties() {
        val client = client(null)
        client.eventNames.subscribe(this.koalaTest)
        client.eventProperties.subscribe(this.propertiesTest)
        val koala = Koala(client)

        koala.trackAppOpen()

        this.koalaTest.assertValue("App Open")

        assertDefaultProperties(null)
    }

    @Test
    fun testDefaultProperties_LoggedInUser() {
        val user = user()
        val client = client(user)
        client.eventNames.subscribe(this.koalaTest)
        client.eventProperties.subscribe(this.propertiesTest)
        val koala = Koala(client)

        koala.trackAppOpen()

        this.koalaTest.assertValue("App Open")

        assertDefaultProperties(user)
        val expectedProperties = propertiesTest.value
        assertEquals(15L, expectedProperties["user_uid"])
    }

    @Test
    fun testDiscoveryProperties() {
        val user = user()
        val client = client(user)
        client.eventNames.subscribe(this.koalaTest)
        client.eventProperties.subscribe(this.propertiesTest)
        val koala = Koala(client)

        val params = DiscoveryParams.builder()
                .staffPicks(true)
                .category(CategoryFactory.artCategory())
                .build()

        koala.trackDiscovery(params, false)

        assertDefaultProperties(user)
        val expectedProperties = propertiesTest.value
        assertEquals(1L, expectedProperties["discover_category_id"])
        assertEquals("Art", expectedProperties["discover_category_name"])
        assertEquals(false, expectedProperties["discover_everything"])
        assertEquals(true, expectedProperties["discover_pwl"])
        assertEquals(false, expectedProperties["discover_recommended"])
        assertEquals("category", expectedProperties["discover_ref_tag"])
        assertEquals(null, expectedProperties["discover_search_term"])
        assertEquals(false, expectedProperties["discover_social"])
        assertEquals(null, expectedProperties["discover_sort"])
        assertNull(expectedProperties["discover_subcategory_id"])
        assertNull(expectedProperties["discover_subcategory_name"])
        assertEquals(null, expectedProperties["discover_tag"])
        assertEquals(false, expectedProperties["discover_watched"])
    }

    @Test
    fun testDiscoveryProperties_AllProjects() {
        val user = user()
        val client = client(user)
        client.eventNames.subscribe(this.koalaTest)
        client.eventProperties.subscribe(this.propertiesTest)
        val koala = Koala(client)

        val params = DiscoveryParams
                .builder()
                .build()

        koala.trackDiscovery(params, false)

        assertDefaultProperties(user)
        val expectedProperties = propertiesTest.value
        assertNull(expectedProperties["discover_category_id"])
        assertNull(expectedProperties["discover_category_name"])
        assertEquals(true, expectedProperties["discover_everything"])
        assertEquals(false, expectedProperties["discover_pwl"])
        assertEquals(false, expectedProperties["discover_recommended"])
        assertEquals("discovery", expectedProperties["discover_ref_tag"])
        assertEquals(null, expectedProperties["discover_search_term"])
        assertEquals(false, expectedProperties["discover_social"])
        assertEquals(null, expectedProperties["discover_sort"])
        assertNull(expectedProperties["discover_subcategory_id"])
        assertNull(expectedProperties["discover_subcategory_name"])
        assertEquals(null, expectedProperties["discover_tag"])
        assertEquals(false, expectedProperties["discover_watched"])
    }

    @Test
    fun testDiscoveryProperties_NoCategory() {
        val user = user()
        val client = client(user)
        client.eventNames.subscribe(this.koalaTest)
        client.eventProperties.subscribe(this.propertiesTest)
        val koala = Koala(client)

        val params = DiscoveryParams
                .builder()
                .sort(DiscoveryParams.Sort.POPULAR)
                .staffPicks(true)
                .build()

        koala.trackDiscovery(params, false)

        assertDefaultProperties(user)
        val expectedProperties = propertiesTest.value
        assertNull(expectedProperties["discover_category_id"])
        assertNull(expectedProperties["discover_category_name"])
        assertEquals(false, expectedProperties["discover_everything"])
        assertEquals(true, expectedProperties["discover_pwl"])
        assertEquals(false, expectedProperties["discover_recommended"])
        assertEquals("recommended_popular", expectedProperties["discover_ref_tag"])
        assertEquals(null, expectedProperties["discover_search_term"])
        assertEquals(false, expectedProperties["discover_social"])
        assertEquals("popularity", expectedProperties["discover_sort"])
        assertNull(expectedProperties["discover_subcategory_id"])
        assertNull(expectedProperties["discover_subcategory_name"])
        assertEquals(null, expectedProperties["discover_tag"])
        assertEquals(false, expectedProperties["discover_watched"])
    }

    @Test
    fun testProjectProperties() {
        val project = project()

        val client = client(null)
        client.eventNames.subscribe(this.koalaTest)
        client.eventProperties.subscribe(this.propertiesTest)
        val koala = Koala(client)

        koala.trackProjectShow(ProjectDataFactory.project(project, RefTag.discovery(), RefTag.recommended()))

        assertDefaultProperties(null)
        assertProjectProperties(project)
        this.koalaTest.assertValues("Project Page")
    }

    @Test
    fun testProjectProperties_LoggedInUser() {
        val project = project()
        val user = user()
        val client = client(user)
        client.eventNames.subscribe(this.koalaTest)
        client.eventProperties.subscribe(this.propertiesTest)
        val koala = Koala(client)

        koala.trackProjectShow(ProjectDataFactory.project(project, RefTag.discovery(), RefTag.recommended()))

        assertDefaultProperties(user)
        assertProjectProperties(project)
        val expectedProperties = propertiesTest.value
        assertEquals(false, expectedProperties["project_user_has_watched"])
        assertEquals(false, expectedProperties["project_user_is_backer"])
        assertEquals(false, expectedProperties["project_user_is_project_creator"])

        this.koalaTest.assertValues("Project Page")
    }

    @Test
    fun testProjectProperties_LoggedInUser_IsBacker() {
        val project = ProjectFactory.backedProject()
                .toBuilder()
                .id(4)
                .category(CategoryFactory.ceramicsCategory())
                .commentsCount(3)
                .creator(creator())
                .location(LocationFactory.unitedStates())
                .updatesCount(5)
                .build()
        val user = user()
        val client = client(user)
        client.eventNames.subscribe(this.koalaTest)
        client.eventProperties.subscribe(this.propertiesTest)
        val koala = Koala(client)

        koala.trackProjectShow(ProjectDataFactory.project(project, RefTag.discovery(), RefTag.recommended()))

        assertDefaultProperties(user)
        assertProjectProperties(project)
        val expectedProperties = propertiesTest.value
        assertEquals(false, expectedProperties["project_user_has_watched"])
        assertEquals(true, expectedProperties["project_user_is_backer"])
        assertEquals(false, expectedProperties["project_user_is_project_creator"])

        this.koalaTest.assertValues("Project Page")
    }

    @Test
    fun testProjectProperties_LoggedInUser_IsProjectCreator() {
        val project = project().toBuilder().build()
        val creator = creator()
        val client = client(creator)
        client.eventNames.subscribe(this.koalaTest)
        client.eventProperties.subscribe(this.propertiesTest)
        val koala = Koala(client)

        koala.trackProjectShow(ProjectDataFactory.project(project, RefTag.discovery(), RefTag.recommended()))

        assertDefaultProperties(creator)
        assertProjectProperties(project)
        val expectedProperties = propertiesTest.value
        assertEquals(false, expectedProperties["project_user_has_watched"])
        assertEquals(false, expectedProperties["project_user_is_backer"])
        assertEquals(true, expectedProperties["project_user_is_project_creator"])

        this.koalaTest.assertValues("Project Page")
    }

    @Test
    fun testProjectProperties_LoggedInUser_HasStarred() {
        val project = project().toBuilder().isStarred(true).build()
        val user = user()
        val client = client(user)
        client.eventNames.subscribe(this.koalaTest)
        client.eventProperties.subscribe(this.propertiesTest)
        val koala = Koala(client)

        koala.trackProjectShow(ProjectDataFactory.project(project, RefTag.discovery(), RefTag.recommended()))

        assertDefaultProperties(user)
        assertProjectProperties(project)
        val expectedProperties = propertiesTest.value
        assertEquals(true, expectedProperties["project_user_has_watched"])
        assertEquals(false, expectedProperties["project_user_is_backer"])
        assertEquals(false, expectedProperties["project_user_is_project_creator"])

        this.koalaTest.assertValues("Project Page")
    }

    private fun assertDefaultProperties(user: User?) {
        val expectedProperties = propertiesTest.value
        assertEquals("9.9.9", expectedProperties["app_version"])
        assertEquals("Google", expectedProperties["brand"])
        assertEquals("android", expectedProperties["client_platform"])
        assertEquals("native", expectedProperties["client_type"])
        assertEquals("uuid", expectedProperties["device_fingerprint"])
        assertEquals("phone", expectedProperties["device_format"])
        assertEquals("Portrait", expectedProperties["device_orientation"])
        assertEquals("uuid", expectedProperties["distinct_id"])
        assertEquals(JSONArray().put("android_example_feature"), expectedProperties["enabled_feature_flags"])
        assertEquals("unavailable", expectedProperties["google_play_services"])
        assertEquals(false, expectedProperties["is_vo_on"])
        assertEquals("kickstarter_android", expectedProperties["koala_lib"])
        assertEquals("Google", expectedProperties["manufacturer"])
        assertEquals("Pixel 3", expectedProperties["model"])
        assertEquals("android", expectedProperties["mp_lib"])
        assertEquals("Android", expectedProperties["os"])
        assertEquals("9", expectedProperties["os_version"])
        assertEquals(DateTime.parse("2018-11-02T18:42:05Z").millis / 1000, expectedProperties["time"])
        assertEquals(user != null, expectedProperties["user_logged_in"])
    }

    private fun assertProjectProperties(project: Project) {
        val expectedProperties = propertiesTest.value
        assertEquals(100, expectedProperties["project_backers_count"])
        assertEquals("Ceramics", expectedProperties["project_subcategory"])
        assertEquals("Art", expectedProperties["project_category"])
        assertEquals(3, expectedProperties["project_comments_count"])
        assertEquals("US", expectedProperties["project_country"])
        assertEquals(3L, expectedProperties["project_creator_uid"])
        assertEquals("USD", expectedProperties["project_currency"])
        assertEquals(50.0, expectedProperties["project_current_pledge_amount"])
        assertEquals(50.0, expectedProperties["project_current_pledge_amount_usd"])
        assertEquals(project.deadline()?.millis?.let { it / 1000 }, expectedProperties["project_deadline"])
        assertEquals(60 * 60 * 24 * 20, expectedProperties["project_duration"])
        assertEquals(100.0, expectedProperties["project_goal"])
        assertEquals(100.0, expectedProperties["project_goal_usd"])
        assertEquals(true, expectedProperties["project_has_video"])
        assertEquals(10 * 24, expectedProperties["project_hours_remaining"])
        assertEquals(true, expectedProperties["project_is_repeat_creator"])
        assertEquals(project.launchedAt()?.millis?.let { it / 1000 }, expectedProperties["project_launched_at"])
        assertEquals("Brooklyn", expectedProperties["project_location"])
        assertEquals("Some Name", expectedProperties["project_name"])
        assertEquals(.5f, expectedProperties["project_percent_raised"])
        assertEquals(4L, expectedProperties["project_pid"])
        assertEquals(50.0, expectedProperties["project_current_pledge_amount"])
        assertEquals(2, expectedProperties["project_rewards_count"])
        assertEquals("live", expectedProperties["project_state"])
        assertEquals(1.0f, expectedProperties["project_static_usd_rate"])
        assertEquals(5, expectedProperties["project_updates_count"])
        assertEquals("discovery", expectedProperties["session_ref_tag"])
        assertEquals("recommended", expectedProperties["session_referrer_credit"])
    }

    private fun client(user: User?) = MockTrackingClient(user?.let { MockCurrentUser(it) }
            ?: MockCurrentUser(), mockCurrentConfig(), TrackingClientType.Type.KOALA, MockExperimentsClientType())

    private fun project() =
            ProjectFactory.project().toBuilder()
                    .id(4)
                    .category(CategoryFactory.ceramicsCategory())
                    .creator(creator())
                    .commentsCount(3)
                    .location(LocationFactory.unitedStates())
                    .updatesCount(5)
                    .build()

    private fun creator() =
            UserFactory.creator().toBuilder()
                    .id(3)
                    .backedProjectsCount(17)
                    .starredProjectsCount(2)
                    .build()

    private fun user() =
            UserFactory.user()
                    .toBuilder()
                    .id(15)
                    .backedProjectsCount(3)
                    .createdProjectsCount(2)
                    .location(LocationFactory.nigeria())
                    .starredProjectsCount(10)
                    .build()

    private fun mockCurrentConfig() = MockCurrentConfig().apply {
        config(ConfigFactory.configWithFeatureEnabled("android_example_feature"))
    }

}
