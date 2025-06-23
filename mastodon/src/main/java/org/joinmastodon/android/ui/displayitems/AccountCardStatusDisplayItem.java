package org.joinmastodon.android.ui.displayitems;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.joinmastodon.android.R;
import org.joinmastodon.android.api.session.AccountSessionManager;
import org.joinmastodon.android.fragments.BaseStatusListFragment;
import org.joinmastodon.android.model.Account;
import org.joinmastodon.android.model.Notification;
import org.joinmastodon.android.model.Relationship;
import org.joinmastodon.android.ui.OutlineProviders;
import org.joinmastodon.android.ui.text.HtmlParser;
import org.joinmastodon.android.ui.utils.CustomEmojiHelper;
import org.joinmastodon.android.ui.utils.UiUtils;
import org.joinmastodon.android.ui.views.ProgressBarButton;

import java.util.Collections;

import me.grishka.appkit.imageloader.ImageLoaderViewHolder;
import me.grishka.appkit.imageloader.requests.ImageLoaderRequest;
import me.grishka.appkit.imageloader.requests.UrlImageLoaderRequest;
import me.grishka.appkit.utils.V;

public class AccountCardStatusDisplayItem extends StatusDisplayItem{
	private final Account account;
	private final Notification notification;
	public ImageLoaderRequest avaRequest, coverRequest;
	public CustomEmojiHelper emojiHelper=new CustomEmojiHelper();
	public CharSequence parsedName, parsedBio;

	public AccountCardStatusDisplayItem(String parentID, BaseStatusListFragment parentFragment, String accountID, Account account, Notification notification){
		super(parentID, parentFragment);
		this.account=account;
		this.notification=notification;
		avaRequest=new UrlImageLoaderRequest(
				TextUtils.isEmpty(account.avatar) ? AccountSessionManager.get(accountID).getDefaultAvatarUrl() : account.avatar,
				V.dp(50), V.dp(50));
		if(!TextUtils.isEmpty(account.header))
			coverRequest=new UrlImageLoaderRequest(account.header, 1000, 1000);
		parsedBio=HtmlParser.parse(account.note, account.emojis, Collections.emptyList(), Collections.emptyList(), parentFragment.getAccountID());
		if(account.emojis.isEmpty()){
			parsedName=account.getDisplayName();
		}else{
			parsedName=HtmlParser.parseCustomEmoji(account.getDisplayName(), account.emojis);
			emojiHelper.setText(new SpannableStringBuilder(parsedName).append(parsedBio));
		}
	}

	@Override
	public Type getType(){
		return Type.ACCOUNT_CARD;
	}

	@Override
	public int getImageCount(){
		return 2+emojiHelper.getImageCount();
	}

	@Override
	public ImageLoaderRequest getImageRequest(int index){
		return switch(index){
			case 0 -> avaRequest;
			case 1 -> coverRequest;
			default -> emojiHelper.getImageRequest(index-2);
		};
	}

	public static class Holder extends StatusDisplayItem.Holder<AccountCardStatusDisplayItem> implements ImageLoaderViewHolder{
		private final ImageView cover, avatar;
		private final TextView name, username, bio, followersCount, followingCount, postsCount, followersLabel, followingLabel, postsLabel;
		private final ProgressBarButton actionButton, acceptButton, rejectButton;
		private final ProgressBar actionProgress, acceptProgress, rejectProgress;
		private final View actionWrap, acceptWrap, rejectWrap;

		private Relationship relationship;

		public Holder(Context context, ViewGroup parent){
			super(context, R.layout.display_item_account_card, parent);

			cover=findViewById(R.id.cover);
			avatar=findViewById(R.id.avatar);
			name=findViewById(R.id.name);
			username=findViewById(R.id.username);
			bio=findViewById(R.id.bio);
			followersCount=findViewById(R.id.followers_count);
			followersLabel=findViewById(R.id.followers_label);
			followingCount=findViewById(R.id.following_count);
			followingLabel=findViewById(R.id.following_label);
			postsCount=findViewById(R.id.posts_count);
			postsLabel=findViewById(R.id.posts_label);
			actionButton=findViewById(R.id.action_btn);
			actionProgress=findViewById(R.id.action_progress);
			actionWrap=findViewById(R.id.action_btn_wrap);
			acceptButton=findViewById(R.id.accept_btn);
			acceptProgress=findViewById(R.id.accept_progress);
			acceptWrap=findViewById(R.id.accept_btn_wrap);
			rejectButton=findViewById(R.id.reject_btn);
			rejectProgress=findViewById(R.id.reject_progress);
			rejectWrap=findViewById(R.id.reject_btn_wrap);

			View card=findViewById(R.id.card);
			card.setOutlineProvider(OutlineProviders.roundedRect(12));
			card.setClipToOutline(true);
			avatar.setOutlineProvider(OutlineProviders.roundedRect(15));
			avatar.setClipToOutline(true);
			View border=findViewById(R.id.avatar_border);
			border.setOutlineProvider(OutlineProviders.roundedRect(17));
			border.setClipToOutline(true);
			cover.setOutlineProvider(OutlineProviders.roundedRect(9));
			cover.setClipToOutline(true);
			actionButton.setOnClickListener(this::onActionButtonClick);
			acceptButton.setOnClickListener(this::onFollowRequestButtonClick);
			rejectButton.setOnClickListener(this::onFollowRequestButtonClick);
			card.setOnClickListener(v->onClick());
		}

		@Override
		public boolean isEnabled(){
			return false;
		}

		@Override
		public void onBind(AccountCardStatusDisplayItem item){
			name.setText(item.parsedName);
			username.setText('@'+item.account.acct);
			bio.setText(item.parsedBio);
			followersCount.setText(UiUtils.abbreviateNumber(item.account.followersCount));
			followingCount.setText(UiUtils.abbreviateNumber(item.account.followingCount));
			postsCount.setText(UiUtils.abbreviateNumber(item.account.statusesCount));
			followersLabel.setText(item.parentFragment.getResources().getQuantityString(R.plurals.followers, (int)Math.min(999, item.account.followersCount)));
			followingLabel.setText(item.parentFragment.getResources().getQuantityString(R.plurals.following, (int)Math.min(999, item.account.followingCount)));
			postsLabel.setText(item.parentFragment.getResources().getQuantityString(R.plurals.sk_posts_count_label, (int)(item.account.statusesCount%1000), item.account.statusesCount));
			followersCount.setVisibility(item.account.followersCount < 0 ? View.GONE : View.VISIBLE);
			followersLabel.setVisibility(item.account.followersCount < 0 ? View.GONE : View.VISIBLE);
			followingCount.setVisibility(item.account.followingCount < 0 ? View.GONE : View.VISIBLE);
			followingLabel.setVisibility(item.account.followingCount < 0 ? View.GONE : View.VISIBLE);
			relationship=item.parentFragment.getRelationship(item.account.id);
			UiUtils.setExtraTextInfo(item.parentFragment.getContext(), null,true, false, false, item.account);

			if(item.notification.type==Notification.Type.FOLLOW_REQUEST && (relationship==null || !relationship.followedBy)){
				actionWrap.setVisibility(View.GONE);
				acceptWrap.setVisibility(View.VISIBLE);
				rejectWrap.setVisibility(View.VISIBLE);
			
				acceptButton.setCompoundDrawableTintList(acceptButton.getTextColors());
				acceptProgress.setIndeterminateTintList(acceptButton.getTextColors());
				rejectButton.setCompoundDrawableTintList(rejectButton.getTextColors());
				rejectProgress.setIndeterminateTintList(rejectButton.getTextColors());
			}else if(relationship==null){
				actionWrap.setVisibility(View.GONE);
				acceptWrap.setVisibility(View.GONE);
				rejectWrap.setVisibility(View.GONE);
			}else{
				actionWrap.setVisibility(View.VISIBLE);
				acceptWrap.setVisibility(View.GONE);
				rejectWrap.setVisibility(View.GONE);
				UiUtils.setRelationshipToActionButtonM3(relationship, actionButton);
			}
		}

		private void onFollowRequestButtonClick(View v) {
			itemView.setHasTransientState(true);
			UiUtils.handleFollowRequest((Activity) v.getContext(), item.account, item.parentFragment.getAccountID(), null, v == acceptButton, relationship, rel -> {
				if(v.getContext()==null || rel==null) return;
				itemView.setHasTransientState(false);
				item.parentFragment.putRelationship(item.account.id, rel);
				RecyclerView.Adapter<? extends RecyclerView.ViewHolder> adapter = getBindingAdapter();
				if (!rel.requested && !rel.followedBy && adapter != null) {
					int index = item.parentFragment.getDisplayItems().indexOf(item);
					item.parentFragment.getDisplayItems().remove(index);
					item.parentFragment.getDisplayItems().remove(index - 1);
					adapter.notifyItemRangeRemoved(getLayoutPosition()-1, 2);
				} else {
					rebind();
				}
			});
		}

		private void onActionButtonClick(View v){
			itemView.setHasTransientState(true);
			UiUtils.performAccountAction((Activity) v.getContext(), item.account, item.parentFragment.getAccountID(), relationship, actionButton, this::setActionProgressVisible, rel->{
				if(v.getContext()==null) return;
				itemView.setHasTransientState(false);
				item.parentFragment.putRelationship(item.account.id, rel);
				rebind();
			});
		}

		private void setActionProgressVisible(boolean visible){
			actionButton.setTextVisible(!visible);
			actionProgress.setVisibility(visible ? View.VISIBLE : View.GONE);
			actionButton.setClickable(!visible);
		}

		@Override
		public void setImage(int index, Drawable image){
			if(index==0){
				avatar.setImageDrawable(image);
			}else if(index==1){
				cover.setImageDrawable(image);
			}else{
				item.emojiHelper.setImageDrawable(index-2, image);
				name.setText(name.getText());
				bio.setText(bio.getText());
			}
			if(image instanceof Animatable && !((Animatable) image).isRunning())
				((Animatable) image).start();
		}

		@Override
		public void clearImage(int index){
			setImage(index, null);
		}
	}
}
