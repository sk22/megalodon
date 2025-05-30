> ### **Not to be confused with [Megalodon](https://github.com/h3poteto/megalodon), the Fediverse API client library for JavaScript**

> ### This project has been abandoned. You may want to migrate to [Moshidon](https://github.com/LucasGGamerM/moshidon) (a fork of Megalodon) or a different Mastodon app.

---

![Pink logo with pink shark](mastodon/src/main/res/mipmap-xhdpi/ic_launcher_round.png)

# Megalodon

[![Translation status](https://translate.codeberg.org/widgets/megalodon/-/svg-badge.svg)](https://translate.codeberg.org/engage/megalodon/)
&nbsp;
[![Download latest release](https://img.shields.io/badge/dynamic/json?color=d92aad&label=Download%20APK&query=%24.tag_name&url=https%3A%2F%2Fapi.github.com%2Frepos%2Fsk22%2Fmegalodon%2Freleases%2Flatest&style=flat)](https://github.com/sk22/megalodon/releases/latest/download/megalodon.apk)

<a href="https://play.google.com/store/apps/details?id=org.joinmastodon.android.sk"><img height="50" alt="Get it on Google Play" src="img/google-play-badge.png"></a>
&nbsp;
<a href="#installation"><img height="50" alt="Get it on IzzyOnDroid" src="img/izzy-badge.png"></a>

> A fork of the [Mastodon Android app](https://github.com/mastodon/mastodon-android) adding important features that are missing in the official app, focusing on [Glitch](https://glitch-soc.github.io/docs) compatibility, a pretty UI and adding new features that I feel make using the Fediverse a more pleasant experience.


## Key features

### **Unlisted posting**

<details>
<p><summary>Allows you to post publicly without having your post show up in trends, hashtags or public timelines (i.e., in the tabs “Community”, “Federated” and “Posts”).</summary></p>

When posting with Unlisted visibility, your posts will still be publicly accessible in your profile. They will also be shown in people’s Home timelines, but only if they follow you or someone they follow reblogged/replied to your post.

The Mastodon documentation has some more information about [Unlisted posting](https://docs.joinmastodon.org/user/posting/#unlisted) and [Public timelines](https://docs.joinmastodon.org/user/network/#timelines).
</details>

### **Federated timeline**

<details>
<p><summary>This allows you to chronologically see all Public posts from people on all other Fediverse neighborhoods your home instance is connected to.</summary></p>

Despite being one of the main features of federated social media, the Federated timeline wasn’t included in the official Mastodon app – supposedly, because this conflicts with Google’s safety requirements for apps on the Play Store.
  
That’s one of the reasons why choosing a small, **well-moderated instance is important**. Instance admins and moderators should always make sure to ban abusive users and stop federating with instances who platform them. On well-moderated instances, the Federated timeline can be a welcoming place to meet new people!
</details>

### **Customizable timelines**

<details>
<p><summary>You can customize Megalodon’s home tab and not only add local and federated timelines, but also pin lists and hashtags.</summary></p>

Even better: You can rename every timeline however you please and pick a distinct icon for each timeline. This way, you can pin the hashtag “#Caturday”, rename your timeline to “CUTENESS OVERLOAD” and set <img src="img/ic_fluent_animal_cat_24_regular.svg" alt="Cat icon from Microsoft Fluent UI icons"> as its icon. :3 You can find the timelines editor by opening your home tab, tapping the `⋮` button in the top right and going to “Edit timelines”.
</details>

### **Draft and schedule posts**

<details>
<p><summary>
Allows to prepare a post and schedule it to send it automatically at a specific time.</summary></p>

You can create drafts, edit them, send them manually later or set a scheduled date. Drafts are technically saved as scheduled posts, so you can view and edit them from other apps that support scheduled posts. Scheduled posts are handled by your home instance, so they'll work even if you uninstall Megalodon.
</details>

## Installation

### Google Play Store

[https://play.google.com/store/apps/details?id=org.joinmastodon.android.sk](https://play.google.com/store/apps/details?id=org.joinmastodon.android.sk)

<a href="https://play.google.com/store/apps/details?id=org.joinmastodon.android.sk"><img height="50" alt="Get it on Google Play" src="img/google-play-badge.png"></a>

### F-Droid via IzzyOnDroid

[https://apt.izzysoft.de/fdroid/index/apk/org.joinmastodon.android.sk](https://apt.izzysoft.de/fdroid/index/apk/org.joinmastodon.android.sk)

<a href="https://apt.izzysoft.de/fdroid/index/apk/org.joinmastodon.android.sk"><img height="50" alt="Get it on IzzyOnDroid" src="img/izzy-badge.png"></a>

Note that you'll need to add Izzy's F-Droid repository to your F-Droid app first:

[`https://apt.izzysoft.de/fdroid/repo`](https://apt.izzysoft.de/fdroid/repo)

### F-Droid via saunarepo

[https://repo.the-sauna.icu](https://repo.the-sauna.icu/)

<a href="https://repo.the-sauna.icu"><img height="28" alt="Get it on SaunaRepo" src="img/saunarepo-badge.svg"></a>

### F-Droid

**[F-Droid.org?](https://f-droid.org)** Not yet, sorry!

If you want, you can help me figure out if something's missing in the [Issue #47: F-Droid.org](https://github.com/sk22/megalodon/issues/47)

### Direct

Press the download button to download the APK. Open the downloaded file on your Android device to install it. Megalodon will automatically notify you about new updates inside the app.

[![Download latest release](https://img.shields.io/badge/dynamic/json?color=d92aad&label=Download%20APK&query=%24.tag_name&url=https%3A%2F%2Fapi.github.com%2Frepos%2Fsk22%2Fmegalodon%2Freleases%2Flatest&style=flat)](https://github.com/sk22/megalodon/releases/latest/download/megalodon.apk)

You might have to accept installing APK files from your browser when trying to install it. You can also take a look at all releases on the [Releases](https://github.com/sk22/megalodon/releases) page.

Megalodon makes use of [Mastodon for Android](https://github.com/mastodon/mastodon-android)’s automatic update checker. Megalodon will check for new updates available on GitHub and offer to download and install them. You can also manually press “Check for updates” at the bottom of the settings page!

---


## Release variants

All downloads can be found on the [Releases](https://github.com/sk22/megalodon/releases) page. When downloading a pre-release, expect to see unfinished features and bugs. If you don’t want that, just download the [latest full release](https://github.com/sk22/megalodon/releases/latest/download/megalodon.apk).

**`megalodon.apk`**

Variant with an integrated updater. If you download Megalodon from here (and not from an app store), just download the regular `megalodon.apk`.

**`upstream-1234abc.apk`**

This is an **unmodified version** of the official [Mastodon for Android](https://github.com/mastodon/mastodon-android) app the respective Megalodon release is based on. Should you find any bugs in Megalodon (which you will), try to see if it occurs with this variant, too. The last 7 digits of the file name are important to know which version of the official app you're using.

<!-- **`megalodon-fdroid.apk`**

Variant without the integrated updater. This is the variant to be published to F-Droid.org where an integrated updater is not necessary. -->

---

## Contribution

### Translation

The translation for the base of the app is sourced from the upstream **Mastodon for Android** project, which you can contribute to on its Crowdin project: [https://crowdin.com/project/mastodon-for-android](https://crowdin.com/project/mastodon-for-android)

There's also a bunch of custom strings exclusive to this project that need to be translated. You can help translate **Megalodon** on Weblate: [https://translate.codeberg.org/projects/megalodon](https://translate.codeberg.org/projects/megalodon)

[![Translation status](https://translate.codeberg.org/widgets/megalodon/-/horizontal-auto.svg)](https://translate.codeberg.org/engage/megalodon)


---


## Detailed changes

### Features

* [Add “Unlisted” as a post visibility option](https://github.com/mastodon/mastodon-android/compare/master...sk22:megalodon:feature/enable-unlisted)
  ([Pull request](https://github.com/mastodon/mastodon-android/pull/103))
* [Add “Federation” tab and change Discover tab order](https://github.com/mastodon/mastodon-android/compare/master...sk22:megalodon:feature/add-federated-timeline) ([Closes issue](https://github.com/mastodon/mastodon-android/issues/8))
* [Add image description button and viewer](https://github.com/mastodon/mastodon-android/compare/master...sk22:megalodon:feature/display-alt-text) ([Pull request](https://github.com/mastodon/mastodon-android/pull/129))
* [Implement pinning posts and displaying pinned posts](https://github.com/mastodon/mastodon-android/compare/master...sk22:megalodon:feature/pin-posts) ([Pull request](https://github.com/mastodon/mastodon-android/pull/140))
* [Implement deleting and re-drafting](https://github.com/mastodon/mastodon-android/compare/master...sk22:megalodon:feature/delete-redraft) ([Closes issue](https://github.com/mastodon/mastodon-android/issues/21))
* [Implement a bookmark button and list](https://github.com/mastodon/mastodon-android/compare/master...sk22:megalodon:feature/bookmarks) ([Closes issue](https://github.com/mastodon/mastodon-android/issues/22))
* [Add “Check for update” button in addition to integrated update checker](https://github.com/mastodon/mastodon-android/compare/master...sk22:megalodon:feature/check-for-update-button)
* [Add “Mark media as sensitive” option](https://github.com/mastodon/mastodon-android/compare/master...sk22:megalodon:feature/mark-media-as-sensitive)
* [Add settings to hide replies and reblogs from the timeline](https://github.com/mastodon/mastodon-android/compare/master...sk22:megalodon:feature/filter-home-timeline) ([Pull request](https://github.com/mastodon/mastodon-android/pull/317))
* [Follow and unfollow hashtags](https://github.com/sk22/megalodon/commit/7d38f031f197aa6cefaf53e39d929538689c1e4e) ([Closes issue](https://github.com/mastodon/mastodon-android/issues/233))
* [Notification bell for posts](https://github.com/sk22/megalodon/commit/b166ca705eb9169025ef32bbe6315b42491b57ea) ([Closes issue](https://github.com/mastodon/mastodon-android/issues/81))
* [Viewing lists and adding/removing users from lists](https://github.com/mastodon/mastodon-android/compare/master...sk22:megalodon:list-timeline-views) based on [@obstsalatschuessel](https://github.com/obstsalatschuessel)'s [Pull request](https://github.com/mastodon/mastodon-android/pull/286)
* [List favorited posts](https://github.com/mastodon/mastodon-android/compare/master...sk22:megalodon:feature/favs-list)
* [Accept/reject follow requests](https://github.com/mastodon/mastodon-android/compare/master...sk22:megalodon:feature/follow-requests)
* [Display content warning title above text](https://github.com/mastodon/mastodon-android/compare/master...sk22:megalodon:feature/cw-above-text)
* [Add notifications tab for posts](https://github.com/mastodon/mastodon-android/compare/master...sk22:megalodon:feature/posts-notifications-tab)
* [Show visibility of original post when replying](https://github.com/mastodon/mastodon-android/compare/master...sk22:megalodon:feature/display-reply-visibility)
* [Clickable reply/boost line above posts](https://github.com/mastodon/mastodon-android/compare/master...sk22:megalodon:clickable-boost-reply-line)
* [Add push notification setting for post notifications](https://github.com/sk22/megalodon/commit/b190480d7739be47f23543d9e7644660f9b4b4ee)
* [Add option to allow voting for multiple options on polls](https://github.com/sk22/megalodon/commit/5b28468efd49387b4f8b83f142f3adf3104ca60c)
* [Add translate function](https://github.com/mastodon/mastodon-android/compare/master...sk22:megalodon:feature/translate-button)
* [Add language selector](https://github.com/mastodon/mastodon-android/compare/master...sk22:megalodon:feature/language-selector)
* [Implement deleting notifications](https://github.com/sk22/megalodon/commit/b0f9ce081f69f29ad59658fc00ca41372cd2677d) (disabled by default)
* [Long-click boost button to "quote" a post](https://github.com/sk22/megalodon/commit/b25a237c20c6a924ed4d9b357999867c3a32b32b)
* [Draft and schedule posts](https://github.com/sk22/megalodon/pull/217)
* [Display original post when replying](https://github.com/sk22/megalodon/commit/375f8ceb2747705fedf43686681cc0e0b812f899)
* [Display server announcements](https://github.com/sk22/megalodon/commit/84179bc207d6b69cc2a770a3c28fa0a39b0b54e8)
* [Create](https://github.com/sk22/megalodon/commit/294595513a45037359b31377aafc25ae5b58d8e7), [edit](https://github.com/sk22/megalodon/commit/d47797bf7ac8cff3f9ba1cfee219a1bb2af21da6) and [delete](https://github.com/sk22/megalodon/commit/54c29fd787fc2cd0dfd2787ad796b8190f795973) lists
* [Soft-blocking (by blocking and immediately unblocking)](https://github.com/sk22/megalodon/commit/e75d350b7a2709259e9fc5138e0e1f361bdb0972)
* [Pinnable custom timelines](https://github.com/sk22/megalodon/pull/338/commits)
* Support for local-only posts
* Support for copying the URL to posts/accounts/… in Pixel launcher’s Recent apps view
* Compatibility for Akkoma Bubble timeline
* Listings of followers/following/favorites/boosts can be loaded from the origin instance (there’s an option to disable this in in the settings)
* Allow opening posts/accounts in-app by sharing a URL/handle to Megalodon (Originally implemented in [Moshidon](https://github.com/LucasGGamerM/moshidon), [PR](https://github.com/sk22/megalodon/pull/531))


### Behavior

* [Make back button return to the home tab before exiting the app](https://github.com/mastodon/mastodon-android/compare/master...sk22:megalodon:feature/back-returns-home) ([Closes issue](https://github.com/mastodon/mastodon-android/issues/118))
* [Always preserve content warnings when replying](https://github.com/mastodon/mastodon-android/compare/master...sk22:megalodon:feature/always-preserve-cw) ([Closes issue](https://github.com/mastodon/mastodon-android/issues/113))
* [Display full image when adding image description](https://github.com/mastodon/mastodon-android/compare/master...sk22:megalodon:feature/compose-image-description-full-image) ([Pull request](https://github.com/mastodon/mastodon-android/pull/182))
* [Set spoiler height independently to content height](https://github.com/mastodon/mastodon-android/compare/master...sk22:megalodon:spoiler-height-independent) ([Closes issue](https://github.com/mastodon/mastodon-android/issues/166))
* [Option to hide interaction numbers](https://github.com/mastodon/mastodon-android/compare/master...sk22:megalodon:settings/hide-interaction-numbers)
* [Option to always reveal content warnings](https://github.com/mastodon/mastodon-android/compare/master...sk22:megalodon:feature/cw-above-text)
* [Option to disable scrolling title bars](https://github.com/mastodon/mastodon-android/compare/master...sk22:megalodon:settings/disable-marquee)
* [No ellipsis for long poll answers](https://github.com/mastodon/mastodon-android/commit/c9aae828e2518adccdc092e41f8d1f0489636271)
* [Show poll vote button for multiple and single answer polls](https://github.com/mastodon/mastodon-android/commit/e14dfda2fdf32f0fa3043504ac5831683a87559a)
* [Show own vote after voting](https://github.com/mastodon/mastodon-android/commit/4ab9e25fec4fd9c10b7a8ddd1be522b3cc12cf28) ([Closes issue](https://github.com/mastodon/mastodon-android/commit/4ab9e25fec4fd9c10b7a8ddd1be522b3cc12cf28))
* [Make inline emoji search case-insensitive and don't only search from start of emoji names](https://github.com/mastodon/mastodon-android/compare/master...sk22:megalodon:better-inline-emoji-search) ([Pull request](https://github.com/mastodon/mastodon-android/pull/445))
* [Include subject line when sharing e.g. a website to Megalodon](https://github.com/mastodon/mastodon-android/compare/master...sk22:megalodon:external-share-include-subject)
* [Improve semantics for voting on polls (radio buttons and checkboxes)](https://github.com/sk22/megalodon/commit/6fd58c96827cb1d2da329cebdc170a1425dd18d7)
* [Copy post URL when long-pressing share button](https://github.com/sk22/megalodon/commit/ba36347f03278763ecec617b1ce57ba89db7be72)
* [Add option to disable swiping between tabs](https://github.com/sk22/megalodon/commit/1f20b21fc84bf006c1ec14bd2229cbfad5215ec8)
* [Resolve Fediverse links in the app](https://github.com/mastodon/mastodon-android/compare/master...sk22:megalodon:feature/open-urls-in-app)
* [Preserve whitespaces in HTML](https://github.com/sk22/megalodon/commit/7d876bddc7a07d98f0fecbf62b13bdb9fcce3412)
* [Long-click to copy links](https://github.com/sk22/megalodon/commit/b32e32274923a94742a9926ef38785f746d41405)
* Improved filtering using Mastodon 4.0 API: [#202](https://github.com/sk22/megalodon/pull/202), [#212](https://github.com/sk22/megalodon/pull/212), [#255](https://github.com/sk22/megalodon/pull/255) by [@thiagojedi](https://github.com/thiagojedi)
* [Support admin notifications](https://github.com/sk22/megalodon/commit/c12a6eaee6b609bc53eb0a45d9199f37d5241801) and [notifications for edited reblogged posts](https://github.com/sk22/megalodon/commit/900e8fb2e9353002c16d15e06b78d2731e121601)
* [Android file opener added back in addition to image picker](https://github.com/sk22/megalodon/commit/3a6ace53d5ab01e28077c9c930cb6ed487b78031)
* [Replies are inserted below the replied-to post in thread view](https://github.com/sk22/megalodon/commit/87c37df370ec24aeea0d2dbaeb29468aa4fb5808)
* Option to auto-reveal equal content warnings in threads


### Visual

* [Custom extended footer redesign](https://github.com/mastodon/mastodon-android/compare/master...sk22:megalodon:compact-extended-footer)
* [Improvements to the true black mode](https://github.com/mastodon/mastodon-android/compare/master...sk22:megalodon:true-black-improvements)
* [Profile header tweaks](https://github.com/mastodon/mastodon-android/compare/master...sk22:megalodon:ui/profile-header-tweaks)
* [Custom color themes](https://github.com/sk22/megalodon/pull/124) by [@LucasGGamerM](https://github.com/LucasGGamerM)
* [Custom "megalodon" text logo](https://github.com/sk22/megalodon/commit/563afd487ca5c608cfbb00fa3909d3c27384acc0) by [@LucasGGamerM](https://github.com/LucasGGamerM)
* [Custom login screen](https://github.com/sk22/megalodon/commit/9bbf8c4618dbe13accaeb3b5482bf3fe88cac4c0)
* [More distinct filled boost icon](https://github.com/sk22/megalodon/commits/more-distinct-filled-boost-icon)
* Material You color theme by [@LucasGGamerM](https://github.com/LucasGGamerM)
* [Animations for interaction buttons](https://github.com/mastodon/mastodon-android/compare/master...sk22:megalodon:feature/animate-buttons)
* [Dedicated icons for different notification types](https://github.com/sk22/megalodon/pull/178) by [@florian-obernberger](https://github.com/florian-obernberger)
* Scale text according to system settings
* Header in timeline for followed hashtags
* [Indicator for missing alt texts](https://github.com/sk22/megalodon/commit/c0c276f03e793b78c478c17dfdef24a66ef7cedb)
* Visually grouped (by removing divider lines and reducing padding) threaded replies in thread view


## Building

As this app is using Java 17 features, you need JDK 17 or newer to build it. Other than that, everything is pretty standard. You can either import the project into Android Studio and build it from there, or run the following command in the project directory:

```
./gradlew assembleRelease
```

Note that Megalodon might be depending on an in-development version of [AppKit](https://github.com/grishka/appkit) – a library by Mastodon for Android’s developer. In case the used AppKit version isn’t published to Maven Central yet, you might have to clone, build and publish it to your local Maven repository. For more information, see [this GitHub issue](https://github.com/mastodon/mastodon-android/issues/375#issuecomment-1507678585).

## License

This project is released under the [GPL-3 License](./LICENSE).

## Links

<a rel="me" href="https://floss.social/@megalodon">@megalodon<wbr>@floss.social</a>
