package org.joinmastodon.android.model;

import android.content.Context;

import com.google.gson.annotations.SerializedName;

import org.joinmastodon.android.R;
import org.joinmastodon.android.api.RequiredField;
import org.joinmastodon.android.ui.utils.UiUtils;

import androidx.annotation.StringRes;

public class PushNotification extends BaseModel{
	public String accessToken;
	public String preferredLocale;
	public long notificationId;
	@RequiredField
	public Type notificationType;
	@RequiredField
	public String icon;
	@RequiredField
	public String title;
	@RequiredField
	public String body;

	public static PushNotification fromNotification(Context context, Notification notification){
		PushNotification pushNotification = new PushNotification();
		pushNotification.notificationType = switch(notification.type) {
			case FOLLOW -> PushNotification.Type.FOLLOW;
			case MENTION -> PushNotification.Type.MENTION;
			case REBLOG -> PushNotification.Type.REBLOG;
			case FAVORITE -> PushNotification.Type.FAVORITE;
			case POLL -> PushNotification.Type.POLL;
			case STATUS -> PushNotification.Type.STATUS;
			case UPDATE -> PushNotification.Type.UPDATE;
			case SIGN_UP -> PushNotification.Type.SIGN_UP;
			case REPORT -> PushNotification.Type.REPORT;
			//Follow request, and reactions are not supported by the API
			default -> throw new IllegalStateException("Unexpected value: "+notification.type);
		};

		String notificationTitle = context.getString(switch(notification.type){
			case FOLLOW -> R.string.user_followed_you;
			case MENTION -> R.string.sk_notification_mention;
			case REBLOG -> R.string.notification_boosted;
			case FAVORITE -> R.string.user_favorited;
			case POLL -> R.string.poll_ended;
			case STATUS -> R.string.sk_posted;
			case UPDATE -> R.string.sk_post_edited;
			case SIGN_UP -> R.string.sk_signed_up;
			case REPORT -> R.string.sk_reported;
			default -> throw new IllegalStateException("Unexpected value: "+notification.type);
		});

		pushNotification.title = UiUtils.generateFormattedString(notificationTitle, notification.account.displayName).toString();
		pushNotification.icon = notification.status.account.avatarStatic;
		pushNotification.body = notification.status.getStrippedText();
		return pushNotification;
	}

	@Override
	public String toString(){
		return "PushNotification{"+
				"accessToken='"+accessToken+'\''+
				", preferredLocale='"+preferredLocale+'\''+
				", notificationId="+notificationId+
				", notificationType="+notificationType+
				", icon='"+icon+'\''+
				", title='"+title+'\''+
				", body='"+body+'\''+
				'}';
	}

	public enum Type{
		@SerializedName("favourite")
		FAVORITE(R.string.notification_type_favorite),
		@SerializedName("mention")
		MENTION(R.string.notification_type_mention),
		@SerializedName("reblog")
		REBLOG(R.string.notification_type_reblog),
		@SerializedName("follow")
		FOLLOW(R.string.notification_type_follow),
		@SerializedName("poll")
		POLL(R.string.notification_type_poll),
		@SerializedName("status")
		STATUS(R.string.sk_notification_type_status),
		@SerializedName("update")
		UPDATE(R.string.sk_notification_type_update),
		@SerializedName("admin.sign_up")
		SIGN_UP(R.string.sk_sign_ups),
		@SerializedName("admin.report")
		REPORT(R.string.sk_new_reports);

		@StringRes
		public final int localizedName;

		Type(int localizedName){
			this.localizedName=localizedName;
		}
	}
}
