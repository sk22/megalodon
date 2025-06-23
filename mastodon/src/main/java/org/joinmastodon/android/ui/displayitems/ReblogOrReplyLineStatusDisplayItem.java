package org.joinmastodon.android.ui.displayitems;

import static org.joinmastodon.android.MastodonApp.context;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joinmastodon.android.R;
import org.joinmastodon.android.api.session.AccountSessionManager;
import org.joinmastodon.android.fragments.BaseStatusListFragment;
import org.joinmastodon.android.model.Account;
import org.joinmastodon.android.model.Emoji;
import org.joinmastodon.android.model.Status;
import org.joinmastodon.android.model.StatusPrivacy;
import org.joinmastodon.android.ui.text.AvatarSpan;
import org.joinmastodon.android.ui.text.CustomEmojiSpan;
import org.joinmastodon.android.ui.text.HtmlParser;
import org.joinmastodon.android.ui.text.SpacerSpan;
import org.joinmastodon.android.ui.utils.CustomEmojiHelper;
import org.joinmastodon.android.ui.utils.UiUtils;

import java.util.List;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;

import me.grishka.appkit.imageloader.ImageLoaderViewHolder;
import me.grishka.appkit.imageloader.requests.ImageLoaderRequest;
import me.grishka.appkit.utils.V;

public class ReblogOrReplyLineStatusDisplayItem extends StatusDisplayItem{
	private CharSequence text;
	@DrawableRes
	private int icon;
	private StatusPrivacy visibility;
	@DrawableRes
	private int iconEnd;
	private CustomEmojiHelper emojiHelper=new CustomEmojiHelper();
	private View.OnClickListener handleClick;
	public boolean needBottomPadding;
	ReblogOrReplyLineStatusDisplayItem extra;
	CharSequence fullText;
	CharSequence boostingTimestamp;

	public ReblogOrReplyLineStatusDisplayItem(String parentID, BaseStatusListFragment parentFragment, CharSequence text, List<Emoji> emojis, @DrawableRes int icon, StatusPrivacy visibility, @Nullable View.OnClickListener handleClick, Status status) {
		this(parentID, parentFragment, text, emojis, icon, visibility, handleClick, text, status, null);
	}

	public ReblogOrReplyLineStatusDisplayItem(String parentID, BaseStatusListFragment parentFragment, CharSequence text, List<Emoji> emojis, @DrawableRes int icon, StatusPrivacy visibility, @Nullable View.OnClickListener handleClick, CharSequence fullText, Status status, Account account) {
		super(parentID, parentFragment);
		SpannableStringBuilder ssb=new SpannableStringBuilder(text);
		if(AccountSessionManager.get(parentFragment.getAccountID()).getLocalPreferences().customEmojiInNames)
			HtmlParser.parseCustomEmoji(ssb, emojis);

		if(status.reblog!=null&&handleClick!=null){
			//add temp chars for span replacement, should be same as spans added below
			ssb.insert(0, "  ");
			ssb.setSpan(new AvatarSpan(status.account), 0, 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
			ssb.setSpan(new SpacerSpan(15, 20), 1, 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
		}
		int replyPrefixLength=context.getString(R.string.in_reply_to).length()-2; //subtract 2 for placeholder
		if(status.inReplyToAccountId!=null&&ssb.length()>replyPrefixLength&&account!=null){
			//add temp chars for span replacement, should be same as spans added below
			ssb.insert(replyPrefixLength, "   ");
			ssb.setSpan(new SpacerSpan(15, 20), replyPrefixLength+1, replyPrefixLength+2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
			ssb.setSpan(new AvatarSpan(account), replyPrefixLength+1, replyPrefixLength+2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
			ssb.setSpan(new SpacerSpan(15, 20), replyPrefixLength+2, replyPrefixLength+3, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
		}
		this.text=ssb;
		emojiHelper.setText(ssb);
		this.fullText=fullText;
		this.icon=icon;
		this.status=status;
		this.handleClick=handleClick;
		TypedValue outValue = new TypedValue();
		context.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
		updateVisibility(visibility);
	}

	public void updateVisibility(StatusPrivacy visibility) {
		this.visibility = visibility;
		this.iconEnd = visibility != null ? switch (visibility) {
			case PUBLIC -> R.drawable.ic_fluent_earth_20sp_regular;
			case UNLISTED -> R.drawable.ic_fluent_lock_open_20sp_regular;
			case PRIVATE -> R.drawable.ic_fluent_lock_closed_20sp_filled;
			default -> 0;
		} : 0;
	}

	@Override
	public Type getType(){
		return Type.REBLOG_OR_REPLY_LINE;
	}

	@Override
	public int getImageCount(){
		return emojiHelper.getImageCount() + (extra!=null ? extra.emojiHelper.getImageCount() : 0);
	}

	@Override
	public ImageLoaderRequest getImageRequest(int index){
		int firstHelperCount=emojiHelper.getImageCount();
		CustomEmojiHelper helper=index<firstHelperCount ? emojiHelper : extra.emojiHelper;
		return helper.getImageRequest(firstHelperCount>0 ? index%firstHelperCount : index);
	}

	public static class Holder extends StatusDisplayItem.Holder<ReblogOrReplyLineStatusDisplayItem> implements ImageLoaderViewHolder{
		private final TextView text, extraText;
		private final View separator;

		public Holder(Activity activity, ViewGroup parent){
			super(activity, R.layout.display_item_reblog_or_reply_line, parent);
			text=findViewById(R.id.text);
			extraText=findViewById(R.id.extra_text);
			separator=findViewById(R.id.separator);
		}

		private void bindLine(ReblogOrReplyLineStatusDisplayItem item, TextView text) {
			text.setText(item.text);
			text.setCompoundDrawablesRelativeWithIntrinsicBounds(item.icon, 0, item.iconEnd, 0);
			text.setOnClickListener(item.handleClick);
			text.setEnabled(!item.inset && item.handleClick != null);
			text.setClickable(!item.inset && item.handleClick != null);
			Context ctx = itemView.getContext();
			int visibilityText = item.visibility != null ? switch (item.visibility) {
				case PUBLIC -> R.string.visibility_public;
				case UNLISTED -> R.string.sk_visibility_unlisted;
				case PRIVATE -> R.string.visibility_followers_only;
				case LOCAL -> R.string.sk_local_only;
				default -> 0;
			} : 0;
			String visibilityDescription=visibilityText!=0 ? " (" + ctx.getString(visibilityText) + ")" : null;
			text.setContentDescription(item.fullText==null && visibilityDescription==null ? null :
					(item.fullText!=null ? item.fullText : item.text)
							+ (visibilityDescription!=null ? visibilityDescription : ""));
			if(Build.VERSION.SDK_INT<Build.VERSION_CODES.N)
				UiUtils.fixCompoundDrawableTintOnAndroid6(text);
			text.setCompoundDrawableTintList(text.getTextColors());
		}

		@Override
		public void onBind(ReblogOrReplyLineStatusDisplayItem item){
			bindLine(item, text);
			if (item.extra != null) bindLine(item.extra, extraText);
			extraText.setVisibility(item.extra == null ? View.GONE : View.VISIBLE);
			separator.setVisibility(item.extra == null ? View.GONE : View.VISIBLE);
			itemView.setPadding(itemView.getPaddingLeft(), itemView.getPaddingTop(), itemView.getPaddingRight(), item.needBottomPadding ? V.dp(16) : 0);
		}

		@Override
		public void setImage(int index, Drawable image){
			int firstHelperCount=item.emojiHelper.getImageCount();
			CustomEmojiHelper helper=index<firstHelperCount ? item.emojiHelper : item.extra.emojiHelper;
			helper.setImageDrawable(firstHelperCount>0 ? index%firstHelperCount : index, image);
			text.invalidate();
			extraText.invalidate();
		}

		@Override
		public void clearImage(int index){
			setImage(index, null);
		}
	}
}
