package org.joinmastodon.android.ui.viewholders;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.TypefaceSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import org.joinmastodon.android.GlobalUserPreferences;
import org.joinmastodon.android.R;
import org.joinmastodon.android.api.requests.accounts.SetAccountFollowed;
import org.joinmastodon.android.api.session.AccountSessionManager;
import org.joinmastodon.android.fragments.ListsFragment;
import org.joinmastodon.android.fragments.ProfileFragment;
import org.joinmastodon.android.fragments.report.ReportReasonChoiceFragment;
import org.joinmastodon.android.model.Account;
import org.joinmastodon.android.model.Relationship;
import org.joinmastodon.android.model.viewmodel.AccountViewModel;
import org.joinmastodon.android.ui.OutlineProviders;
import org.joinmastodon.android.ui.text.HtmlParser;
import org.joinmastodon.android.ui.utils.UiUtils;
import org.joinmastodon.android.ui.views.CheckableRelativeLayout;
import org.joinmastodon.android.ui.views.ProgressBarButton;
import org.parceler.Parcels;

import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import me.grishka.appkit.Nav;
import me.grishka.appkit.api.Callback;
import me.grishka.appkit.api.ErrorResponse;
import me.grishka.appkit.imageloader.ImageLoaderViewHolder;
import me.grishka.appkit.utils.BindableViewHolder;
import me.grishka.appkit.views.UsableRecyclerView;

public class AccountViewHolder extends BindableViewHolder<AccountViewModel> implements ImageLoaderViewHolder, UsableRecyclerView.Clickable, UsableRecyclerView.LongClickable{
	private final TextView name, username, followers, pronouns, bio;
	private final ImageView avatar;
	private final FrameLayout accessory;
	private final ProgressBarButton button;
	private final PopupMenu contextMenu;
	private final View menuAnchor;
	private final TypefaceSpan mediumSpan=new TypefaceSpan("sans-serif-medium");
	private final CheckableRelativeLayout view;
	private final View checkbox;
	private final ProgressBar actionProgress;

	private final String accountID;
	private final Fragment fragment;
	private final HashMap<String, Relationship> relationships;

	private Consumer<AccountViewHolder> onClick;
	private AccessoryType accessoryType;
	private boolean showBio;
	private boolean checked;

	public AccountViewHolder(Fragment fragment, ViewGroup list, HashMap<String, Relationship> relationships){
		super(fragment.getActivity(), R.layout.item_account_list, list);
		this.fragment=fragment;
		this.accountID=Objects.requireNonNull(fragment.getArguments().getString("account"));
		this.relationships=relationships;

		view=(CheckableRelativeLayout) itemView;
		name=findViewById(R.id.name);
		username=findViewById(R.id.username);
		avatar=findViewById(R.id.avatar);
		accessory=findViewById(R.id.accessory);
		button=findViewById(R.id.button);
		menuAnchor=findViewById(R.id.menu_anchor);
		followers=findViewById(R.id.followers_count);
		pronouns=findViewById(R.id.pronouns);
		bio=findViewById(R.id.bio);
		checkbox=findViewById(R.id.checkbox);
		actionProgress=findViewById(R.id.action_progress);

		avatar.setOutlineProvider(OutlineProviders.roundedRect(10));
		avatar.setClipToOutline(true);

		button.setOnClickListener(this::onButtonClick);
		accessory.setOnClickListener(v -> button.performClick());

		contextMenu=new PopupMenu(fragment.getActivity(), menuAnchor);
		contextMenu.inflate(R.menu.profile);
		contextMenu.setOnMenuItemClickListener(this::onContextMenuItemSelected);
		if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.P && !UiUtils.isEMUI())
			contextMenu.getMenu().setGroupDividerEnabled(true);
		UiUtils.enablePopupMenuIcons(fragment.getContext(), contextMenu);

		setStyle(AccessoryType.BUTTON, false);
	}

	@SuppressLint("SetTextI18n")
	@Override
	public void onBind(AccountViewModel item){
		name.setText(item.parsedName);
		username.setText("@"+item.account.acct);
		long num=item.account.followersCount > -1 ? item.account.followersCount : item.account.followingCount;
		String followersStr = fragment.getResources().getQuantityString(item.account.followersCount > -1
				? R.plurals.x_followers : R.plurals.x_following, num>1000 ? 999 : (int)num);
		String followersNum=UiUtils.abbreviateNumber(num);
		int index=followersStr.indexOf("%,d");
		followersStr=followersStr.replace("%,d", followersNum);
		SpannableStringBuilder followersFormatted=new SpannableStringBuilder(followersStr);
		if(index!=-1){
			followersFormatted.setSpan(mediumSpan, index, index+followersNum.length(), 0);
		}
		if (item.account.followingCount > -1 || item.account.followersCount > -1) {
			followers.setVisibility(View.VISIBLE);
			followers.setText(followersFormatted);
		} else {
			followers.setVisibility(View.GONE);
		}

		// you know what's cooler than followers or verified links? yep. pronouns
		Optional<String> pronounsString=GlobalUserPreferences.displayPronounsInUserListings
				? UiUtils.extractPronouns(itemView.getContext(), item.account) : Optional.empty();
		pronouns.setVisibility(pronounsString.isPresent() ? View.VISIBLE : View.GONE);
		pronounsString.ifPresent(p -> HtmlParser.setTextWithCustomEmoji(pronouns, p, item.account.emojis));

		/* unused in megalodon
		boolean hasVerifiedLink=item.verifiedLink!=null;
		if(!hasVerifiedLink)
			verifiedLink.setText(R.string.no_verified_link);
		else
			verifiedLink.setText(item.verifiedLink);
		verifiedLink.setCompoundDrawablesRelativeWithIntrinsicBounds(hasVerifiedLink ? R.drawable.ic_fluent_checkmark_16_filled : R.drawable.ic_help_16px, 0, 0, 0);
		int tintColor=UiUtils.getThemeColor(fragment.getActivity(), hasVerifiedLink ? R.attr.colorM3Primary : R.attr.colorM3Secondary);
		verifiedLink.setTextColor(tintColor);
		verifiedLink.setCompoundDrawableTintList(ColorStateList.valueOf(tintColor));
		*/
		bindRelationship();
		if(showBio){
			bio.setText(item.parsedBio);
		}
	}

	public void bindRelationship(){
		if(relationships==null || accessoryType!=AccessoryType.BUTTON)
			return;
		Relationship rel=relationships.get(item.account.id);
		if(rel==null || AccountSessionManager.getInstance().isSelf(accountID, item.account)){
			button.setVisibility(View.GONE);
		}else{
			button.setVisibility(View.VISIBLE);
			UiUtils.setRelationshipToActionButtonM3(rel, button);
		}
	}

	@Override
	public void setImage(int index, Drawable image){
		if(index==0){
			avatar.setImageDrawable(image);
		}else{
			item.emojiHelper.setImageDrawable(index-1, image);
			name.setText(name.getText());
			bio.setText(bio.getText());
		}

		if(image instanceof Animatable a && !a.isRunning())
			a.start();
	}

	@Override
	public void clearImage(int index){
		if(index==0){
			avatar.setImageResource(R.drawable.image_placeholder);
		}else{
			setImage(index, null);
		}
	}

	@Override
	public void onClick(){
		if(onClick!=null){
			onClick.accept(this);
			return;
		}
		Bundle args=new Bundle();
		args.putString("account", accountID);
		if (item.account.isRemote)
			args.putParcelable("remoteAccount", Parcels.wrap(item.account));
		else
			args.putParcelable("profileAccount", Parcels.wrap(item.account));
		Nav.go(fragment.getActivity(), ProfileFragment.class, args);
	}

	@Override
	public boolean onLongClick(){
		return false;
	}

	@Override
	public boolean onLongClick(float x, float y){
		if(relationships==null)
			return false;
		Relationship relationship=relationships.get(item.account.id);
		if(relationship==null)
			return false;
		Menu menu=contextMenu.getMenu();
		Account account=item.account;

		menu.findItem(R.id.edit_note).setVisible(false);
		menu.findItem(R.id.manage_user_lists).setTitle(fragment.getString(R.string.sk_lists_with_user, account.getShortUsername()));
		MenuItem mute=menu.findItem(R.id.mute);
		mute.setTitle(fragment.getString(relationship.muting ? R.string.unmute_user : R.string.mute_user, account.getShortUsername()));
		mute.setIcon(relationship.muting ? R.drawable.ic_fluent_speaker_0_24_regular : R.drawable.ic_fluent_speaker_off_24_regular);
		UiUtils.insetPopupMenuIcon(fragment.getContext(), mute);
		menu.findItem(R.id.block).setTitle(fragment.getString(relationship.blocking ? R.string.unblock_user : R.string.block_user, account.getShortUsername()));
		menu.findItem(R.id.report).setTitle(fragment.getString(R.string.report_user, account.getShortUsername()));
		menu.findItem(R.id.manage_user_lists).setVisible(relationship.following);
		menu.findItem(R.id.soft_block).setVisible(relationship.followedBy && !relationship.following);
		MenuItem hideBoosts=menu.findItem(R.id.hide_boosts);
		if(relationship.following){
			hideBoosts.setTitle(fragment.getString(relationship.showingReblogs ? R.string.hide_boosts_from_user : R.string.show_boosts_from_user, account.getShortUsername()));
			hideBoosts.setIcon(relationship.showingReblogs ? R.drawable.ic_fluent_arrow_repeat_all_off_24_regular : R.drawable.ic_fluent_arrow_repeat_all_24_regular);
			UiUtils.insetPopupMenuIcon(fragment.getContext(), hideBoosts);
			hideBoosts.setVisible(true);
		}else{
			hideBoosts.setVisible(false);
		}
		MenuItem blockDomain=menu.findItem(R.id.block_domain);
		if(!account.isLocal()){
			blockDomain.setTitle(fragment.getString(relationship.domainBlocking ? R.string.unblock_domain : R.string.block_domain, account.getDomain()));
			blockDomain.setVisible(true);
		}else{
			blockDomain.setVisible(false);
		}

		menuAnchor.setTranslationX(x);
		menuAnchor.setTranslationY(y);
		contextMenu.show();

		return true;
	}

	private void onButtonClick(View v){
		if(relationships==null)
			return;
		itemView.setHasTransientState(true);
		UiUtils.performAccountAction((Activity) v.getContext(), item.account, accountID, relationships.get(item.account.id), button, this::setActionProgressVisible, rel->{
			itemView.setHasTransientState(false);
			relationships.put(item.account.id, rel);
			bindRelationship();
		});
	}

	private void setActionProgressVisible(boolean visible){
		if(visible)
			actionProgress.setIndeterminateTintList(button.getTextColors());
//		TODO button.setTextVisible(!visible);
		actionProgress.setVisibility(visible ? View.VISIBLE : View.GONE);
		button.setClickable(!visible);
	}

	private boolean onContextMenuItemSelected(MenuItem item){
		Relationship relationship=relationships.get(this.item.account.id);
		if(relationship==null)
			return false;
		Account account=this.item.account;

		int id=item.getItemId();
		if(id==R.id.share){
			Intent intent=new Intent(Intent.ACTION_SEND);
			intent.setType("text/plain");
			intent.putExtra(Intent.EXTRA_TEXT, account.url);
			fragment.startActivity(Intent.createChooser(intent, item.getTitle()));
		}else if(id==R.id.mute){
			UiUtils.confirmToggleMuteUser(fragment.getActivity(), accountID, account, relationship.muting, this::updateRelationship);
		}else if(id==R.id.block){
			UiUtils.confirmToggleBlockUser(fragment.getActivity(), accountID, account, relationship.blocking, this::updateRelationship);
		}else if(id==R.id.soft_block){
			UiUtils.confirmSoftBlockUser(fragment.getActivity(), accountID, account, this::updateRelationship);
		}else if(id==R.id.report){
			Bundle args=new Bundle();
			args.putString("account", accountID);
			args.putParcelable("reportAccount", Parcels.wrap(account));
			args.putParcelable("relationship", Parcels.wrap(relationship));
			Nav.go(fragment.getActivity(), ReportReasonChoiceFragment.class, args);
		}else if(id==R.id.open_in_browser){
			UiUtils.launchWebBrowser(fragment.getActivity(), account.url);
		}else if(id==R.id.block_domain){
			UiUtils.confirmToggleBlockDomain(fragment.getActivity(), accountID, account.getDomain(), relationship.domainBlocking, ()->{
				relationship.domainBlocking=!relationship.domainBlocking;
				bindRelationship();
			});
		}else if(id==R.id.hide_boosts){
			new SetAccountFollowed(account.id, true, !relationship.showingReblogs)
					.setCallback(new Callback<>(){
						@Override
						public void onSuccess(Relationship result){
							relationships.put(AccountViewHolder.this.item.account.id, result);
							bindRelationship();
						}

						@Override
						public void onError(ErrorResponse error){
							error.showToast(fragment.getActivity());
						}
					})
					.wrapProgress(fragment.getActivity(), R.string.loading, false)
					.exec(accountID);
		}else if(id==R.id.manage_user_lists){
			final Bundle args=new Bundle();
			args.putString("account", accountID);
			args.putString("profileAccount", account.id);
			args.putString("profileDisplayUsername", account.getDisplayUsername());
			Nav.go(fragment.getActivity(), ListsFragment.class, args);
		}
		return true;
	}

	private void updateRelationship(Relationship r){
		relationships.put(item.account.id, r);
		bindRelationship();
	}

	public void setOnClickListener(Consumer<AccountViewHolder> listener){
		onClick=listener;
	}

	public void setStyle(AccessoryType accessoryType, boolean showBio){
		if(accessoryType!=this.accessoryType){
			this.accessoryType=accessoryType;
			switch(accessoryType){
				case NONE -> {
					button.setVisibility(View.GONE);
					checkbox.setVisibility(View.GONE);
				}
				case CHECKBOX -> {
					button.setVisibility(View.GONE);
					checkbox.setVisibility(View.VISIBLE);
					checkbox.setBackground(new CheckBox(checkbox.getContext()).getButtonDrawable());
				}
				case RADIOBUTTON -> {
					button.setVisibility(View.GONE);
					checkbox.setVisibility(View.VISIBLE);
					checkbox.setBackground(new RadioButton(checkbox.getContext()).getButtonDrawable());
				}
				case BUTTON -> {
					button.setVisibility(View.VISIBLE);
					checkbox.setVisibility(View.GONE);
				}
			}
			view.setCheckable(accessoryType==AccessoryType.CHECKBOX || accessoryType==AccessoryType.RADIOBUTTON);
		}
		this.showBio=showBio;
		bio.setVisibility(showBio ? View.VISIBLE : View.GONE);
	}

	public void setChecked(boolean checked){
		this.checked=checked;
		view.setChecked(checked);
	}

	public enum AccessoryType{
		NONE,
		BUTTON,
		CHECKBOX,
		RADIOBUTTON
	}
}
