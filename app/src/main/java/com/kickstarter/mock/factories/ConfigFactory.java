package com.kickstarter.mock.factories;

import com.kickstarter.libs.Config;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import androidx.annotation.NonNull;

public final class ConfigFactory {
  private ConfigFactory() {}

  public static @NonNull Config config() {
    final Config.LaunchedCountry US = Config.LaunchedCountry.builder()
      .name("US")
      .currencyCode("USD")
      .currencySymbol("$")
      .trailingCode(true)
      .build();

    final Config.LaunchedCountry GB = Config.LaunchedCountry.builder()
      .name("GB")
      .currencyCode("GBP")
      .currencySymbol("£")
      .trailingCode(false)
      .build();

    final Config.LaunchedCountry CA = Config.LaunchedCountry.builder()
      .name("CA")
      .currencyCode("CAD")
      .currencySymbol("$")
      .trailingCode(true)
      .build();

    return Config.builder()
      .countryCode("US")
      .features(Collections.emptyMap())
      .launchedCountries(Arrays.asList(US, GB, CA))
      .build();
  }

  public static @NonNull Config configForUSUser() {
    return config();
  }

  public static @NonNull Config configForCAUser() {
    return config().toBuilder()
      .countryCode("CA")
      .build();
  }

  public static @NonNull Config configForITUser() {
    return config().toBuilder()
      .countryCode("IT")
      .build();
  }

  public static @NonNull Config configWithExperiment(final @NonNull String experiment, final @NonNull String variant) {
    return config().toBuilder()
      .abExperiments(Collections.singletonMap(experiment, variant))
      .build();
  }

  public static @NonNull Config configWithExperiments(final @NonNull Map<String, String> abExperiments) {
    return config().toBuilder()
      .abExperiments(abExperiments)
      .build();
  }

  public static @NonNull Config configWithFeatureEnabled(final @NonNull String featureKey) {
    return config().toBuilder()
      .features(Collections.singletonMap(featureKey, true))
      .build();
  }

  public static @NonNull Config configWithFeaturesEnabled(final @NonNull Map<String, Boolean> features) {
    return config().toBuilder()
      .features(features)
      .build();
  }
}
