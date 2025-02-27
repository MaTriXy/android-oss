package com.kickstarter.ui.activities.compose.projectpage

import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import com.kickstarter.R
import com.kickstarter.models.AiDisclosure
import com.kickstarter.ui.compose.designsystem.KSClickableText
import com.kickstarter.ui.compose.designsystem.KSDividerLineGrey
import com.kickstarter.ui.compose.designsystem.KSTheme
import com.kickstarter.ui.compose.designsystem.KSTheme.colors
import com.kickstarter.ui.compose.designsystem.KSTheme.dimensions
import com.kickstarter.ui.compose.designsystem.KSTheme.typographyV2
import com.kickstarter.viewmodels.projectpage.ProjectAIViewModel

@Composable
@Preview(name = "Light", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
fun AiDisclosureScreenPreview() {
    val aiDisclosure = AiDisclosure.builder()
        .fundingForAiOption(true)
        .fundingForAiConsent(true)
        .generatedByAiConsent("Some generated consent, generated by the creator")
        .generatedByAiDetails("Some generated details, generated by the creator")
        .otherAiDetails("Other details to include, generated by the creator")
        .build()

    KSTheme {
        AiDisclosureScreen(
            state = ProjectAIViewModel.UiState(aiDisclosure = aiDisclosure),
            clickCallback = {}
        )
    }
}

enum class TestTag {
    TILE_TAG,
    FOUNDING_SECTION_TITLE,
    FOUNDING_SECTION_ATTRIBUTION,
    FOUNDING_SECTION_CONSENT,
    FOUNDING_SECTION_OPTION,
    FOUNDING_SECTION_DIVIDER,
    GENERATION_SECTION_TITLE,
    GENERATION_SECTION_CONSENT_QUESTION,
    GENERATION_SECTION_CONSENT_DIVIDER,
    GENERATION_SECTION_CONSENT,
    GENERATION_SECTION_DETAILS_QUESTION,
    GENERATION_SECTION_DETAILS_DIVIDER,
    GENERATION_SECTION_DETAILS,
    GENERATION_SECTION_DIVIDER,
    OTHER_SECTION_TITLE,
    OTHER_SECTION_DETAILS,
    OTHER_SECTION_DIVIDER,
    LINK
}

@Composable
fun AiDisclosureScreen(
    state: ProjectAIViewModel.UiState,
    clickCallback: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .systemBarsPadding()
            .background(colors.kds_white)
            .padding(
                PaddingValues(
                    start = dimensions.paddingMediumLarge,
                    end = dimensions.paddingMedium
                )
            )
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(dimensions.paddingMediumSmall)
    ) {
        Spacer(modifier = Modifier.height(dimensions.paddingMedium))

        Text(
            text = stringResource(id = R.string.Use_of_ai),
            style = typographyV2.title2Bold,
            color = colors.kds_support_700,
            modifier = Modifier
                .testTag(TestTag.TILE_TAG.name)
        )

        InvolvesFundingSection(state)

        InvolvesGenerationSection(state)

        InvolvesOtherSection(state)

        KSClickableText(
            modifier = Modifier
                .testTag(TestTag.LINK.name),
            resourceId = R.string.Learn_about_AI_policy_on_Kickstarter,
            clickCallback = clickCallback
        )

        Spacer(modifier = Modifier.height(dimensions.paddingDoubleLarge))
    }
}

@Composable
private fun InvolvesOtherSection(state: ProjectAIViewModel.UiState) {
    val otherDetails = state.aiDisclosure?.otherAiDetails ?: ""
    if (otherDetails.isNotEmpty()) {
        Text(
            text = stringResource(id = R.string.I_am_incorporating_AI_in_my_project),
            style = typographyV2.headLine,
            color = colors.kds_support_700,
            modifier = Modifier
                .testTag(TestTag.OTHER_SECTION_TITLE.name)
        )

        Text(
            text = otherDetails,
            style = typographyV2.footNote,
            color = colors.kds_support_700,
            modifier = Modifier
                .testTag(TestTag.OTHER_SECTION_DETAILS.name)
        )

        KSDividerLineGrey(
            modifier = Modifier
                .testTag(TestTag.OTHER_SECTION_DIVIDER.name)
        )
    }
}

@Composable
private fun InvolvesGenerationSection(state: ProjectAIViewModel.UiState) {
    val details = state.aiDisclosure?.generatedByAiDetails ?: ""
    val consent = state.aiDisclosure?.generatedByAiConsent ?: ""
    if (details.isNotEmpty() || consent.isNotEmpty()) {
        Text(
            text = stringResource(id = R.string.I_plan_to_use_AI_generated_content),
            style = typographyV2.headLine,
            color = colors.kds_support_700,
            modifier = Modifier
                .testTag(TestTag.GENERATION_SECTION_TITLE.name)
        )

        Text(
            text = stringResource(id = R.string.What_parts_of_your_project_will_use_AI_generated_content),
            style = typographyV2.headingSM,
            color = colors.kds_support_700,
            modifier = Modifier.testTag(TestTag.GENERATION_SECTION_DETAILS_QUESTION.name)
        )

        Row(
            modifier = Modifier.height(intrinsicSize = IntrinsicSize.Min)
        ) {
            Divider(
                color = colors.kds_create_700,
                modifier = Modifier
                    .fillMaxHeight()
                    .width(dimensions.verticalDividerWidth)
                    .testTag(TestTag.GENERATION_SECTION_DETAILS_DIVIDER.name)
            )

            Spacer(modifier = Modifier.width(dimensions.paddingMediumSmall))

            Text(
                text = details,
                style = typographyV2.footNote,
                color = colors.kds_support_700,
                modifier = Modifier
                    .testTag(TestTag.GENERATION_SECTION_DETAILS.name)
            )
        }

        Text(
            text = stringResource(id = R.string.Do_you_have_the_consent_of_the_owners_of_the_works_used_for_AI),
            style = typographyV2.headingSM,
            color = colors.kds_support_700,
            modifier = Modifier.testTag(TestTag.GENERATION_SECTION_CONSENT_QUESTION.name)
        )

        Row(
            modifier = Modifier.height(intrinsicSize = IntrinsicSize.Min)
        ) {
            Divider(
                color = colors.kds_create_700,
                modifier = Modifier
                    .fillMaxHeight()
                    .width(dimensions.verticalDividerWidth)
                    .testTag(TestTag.GENERATION_SECTION_CONSENT_DIVIDER.name)
            )

            Spacer(modifier = Modifier.width(dimensions.paddingMediumSmall))

            Text(
                text = consent,
                style = typographyV2.footNote,
                color = colors.kds_support_700,
                modifier = Modifier
                    .testTag(TestTag.GENERATION_SECTION_CONSENT.name)
            )
        }

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = colors.kds_support_300
                )
                .height(dimensions.dividerThickness)
                .testTag(TestTag.GENERATION_SECTION_DIVIDER.name)
        )
    }
}

@Composable
private fun InvolvesFundingSection(state: ProjectAIViewModel.UiState) {
    val fundingAiAttr = state.aiDisclosure?.fundingForAiAttribution ?: false
    val fundingAiConsent = state.aiDisclosure?.fundingForAiConsent ?: false
    val fundingAiOption = state.aiDisclosure?.fundingForAiOption ?: false

    if (fundingAiAttr || fundingAiConsent || fundingAiOption) {
        Text(
            text = stringResource(id = R.string.My_project_seeks_funding_for_AI_technology),
            style = typographyV2.headLine,
            color = colors.kds_support_700,
            modifier = Modifier
                .testTag(TestTag.FOUNDING_SECTION_TITLE.name)
        )

        if (fundingAiConsent) AiDisclosureRow(
            stringResId = R.string.For_the_database_or_source_I_will_use,
            testTag = TestTag.FOUNDING_SECTION_CONSENT.name
        )
        if (fundingAiAttr) AiDisclosureRow(
            stringResId = R.string.The_owners_of_those_works,
            testTag = TestTag.FOUNDING_SECTION_ATTRIBUTION.name

        )
        if (fundingAiOption) AiDisclosureRow(
            stringResId = R.string.There_is_or_will_be_an_opt,
            testTag = TestTag.FOUNDING_SECTION_OPTION.name
        )

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = colors.kds_support_300
                )
                .height(dimensions.dividerThickness)
                .testTag(TestTag.FOUNDING_SECTION_DIVIDER.name)
        )
    }
}

@Composable
fun AiDisclosureRow(
    @DrawableRes iconId: Int = R.drawable.icon__check_green,
    @StringRes stringResId: Int,
    testTag: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(dimensions.paddingSmall)
    ) {
        Image(
            modifier = Modifier
                .width(dimensions.iconSizeMedium)
                .height(dimensions.iconSizeMedium),
            imageVector = ImageVector.vectorResource(
                id = iconId
            ),
            contentDescription = null,
            colorFilter = ColorFilter.tint(colors.kds_create_700)
        )
        Text(
            text = stringResource(id = stringResId),
            style = typographyV2.footNote,
            color = colors.kds_support_700,
            modifier = Modifier.testTag(testTag)
        )
    }
}
