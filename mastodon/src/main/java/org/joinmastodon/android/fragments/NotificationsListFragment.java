package org.joinmastodon.android.fragments;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.squareup.otto.Subscribe;

import org.joinmastodon.android.E;
import org.joinmastodon.android.R;
import org.joinmastodon.android.api.session.AccountSessionManager;
import org.joinmastodon.android.events.PollUpdatedEvent;
import org.joinmastodon.android.events.RemoveAccountPostsEvent;
import org.joinmastodon.android.events.StatusCountersUpdatedEvent;
import org.joinmastodon.android.model.Notification;
import org.joinmastodon.android.model.PaginatedResponse;
import org.joinmastodon.android.model.Status;
import org.joinmastodon.android.ui.displayitems.ExtendedFooterStatusDisplayItem;
import org.joinmastodon.android.ui.displayitems.FooterStatusDisplayItem;
import org.joinmastodon.android.ui.displayitems.NotificationHeaderStatusDisplayItem;
import org.joinmastodon.android.ui.displayitems.StatusDisplayItem;
import org.joinmastodon.android.ui.utils.DiscoverInfoBannerHelper;
import org.joinmastodon.android.ui.utils.InsetStatusItemDecoration;
import org.joinmastodon.android.ui.utils.UiUtils;
import org.joinmastodon.android.utils.ObjectIdComparator;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import me.grishka.appkit.api.SimpleCallback;

public class NotificationsListFragment extends BaseStatusListFragment<Notification>{
	private boolean onlyMentions;
	private boolean onlyPosts;
	private String maxID;
	private boolean reloadingFromCache;
	private final DiscoverInfoBannerHelper bannerHelper = new DiscoverInfoBannerHelper(DiscoverInfoBannerHelper.BannerType.POST_NOTIFICATIONS);

	@Override
	protected boolean wantsComposeButton() {
		return false;
	}

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		E.register(this);
		if(savedInstanceState!=null){
			onlyMentions=savedInstanceState.getBoolean("onlyMentions", false);
			onlyPosts=getArguments().getBoolean("onlyPosts", false);
		}
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
		E.unregister(this);
	}

	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		setTitle(R.string.notifications);
	}

	@Override
	protected List<StatusDisplayItem> buildDisplayItems(Notification n){
		NotificationHeaderStatusDisplayItem titleItem;
		if(n.type==Notification.Type.MENTION || n.type==Notification.Type.STATUS){
			titleItem=null;
		}else{
			titleItem=new NotificationHeaderStatusDisplayItem(n.id, this, n, accountID);
		}
		if(n.status!=null){
			int flags=titleItem==null ? 0 : (StatusDisplayItem.FLAG_NO_FOOTER | StatusDisplayItem.FLAG_INSET); // | StatusDisplayItem.FLAG_NO_HEADER);
			ArrayList<StatusDisplayItem> items=StatusDisplayItem.buildItems(this, n.status, accountID, n, knownAccounts, null, flags);
			if(titleItem!=null)
				items.add(0, titleItem);
			return items;
		}else if(titleItem!=null){
			return Collections.singletonList(titleItem);
		}else{
			return Collections.emptyList();
		}
	}
	@Override
	protected void addAccountToKnown(Notification s){
		if(!knownAccounts.containsKey(s.account.id))
			knownAccounts.put(s.account.id, s.account);
		if(s.status!=null && !knownAccounts.containsKey(s.status.account.id))
			knownAccounts.put(s.status.account.id, s.status.account);
		if(s.status!=null && s.status.reblog!=null && !knownAccounts.containsKey(s.status.reblog.account.id))
			knownAccounts.put(s.status.reblog.account.id, s.status.reblog.account);
	}

	@Override
	protected void doLoadData(int offset, int count){
		AccountSessionManager.getInstance()
				.getAccount(accountID).getCacheController()
				.getNotifications(offset>0 ? maxID : null, count, onlyMentions, onlyPosts, refreshing && !reloadingFromCache, new SimpleCallback<>(this){
					@Override
					public void onSuccess(PaginatedResponse<List<Notification>> result){
						if(getActivity()==null)
							return;
						onDataLoaded(result.items.stream().filter(n->n.type!=null).collect(Collectors.toList()), !result.items.isEmpty());
						maxID=result.maxID;
						reloadingFromCache=false;
					}
				});
	}

	@Override
	protected void onShown(){
		super.onShown();
		if(!dataLoading){
			if(onlyMentions){
				refresh();
			}else{
				reloadingFromCache=true;
				refresh();
			}
		}
	}

	@Override
	protected void onHidden(){
		super.onHidden();
		resetUnreadBackground();
	}

	@Override
	public void onItemClick(String id){
		Notification n=getNotificationByID(id);
		Bundle args = new Bundle();
		if(n.status != null && n.status.inReplyToAccountId != null && knownAccounts.containsKey(n.status.inReplyToAccountId))
			args.putParcelable("inReplyToAccount", Parcels.wrap(knownAccounts.get(n.status.inReplyToAccountId)));
		UiUtils.showFragmentForNotification(getContext(), n, accountID, args);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState){
		super.onViewCreated(view, savedInstanceState);
		list.addItemDecoration(new InsetStatusItemDecoration(this));
		if (onlyPosts) bannerHelper.maybeAddBanner(contentWrap);

		list.addItemDecoration(new RecyclerView.ItemDecoration(){
			private Paint paint=new Paint();
			private Rect tmpRect=new Rect();

			{
				paint.setColor(UiUtils.getThemeColor(getActivity(), R.attr.colorM3SurfaceVariant));
			}

			@Override
			public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state){
				if (getParentFragment() instanceof NotificationsFragment nf) {
					if(TextUtils.isEmpty(nf.unreadMarker))
						return;
					for(int i=0;i<parent.getChildCount();i++){
						View child=parent.getChildAt(i);
						if(parent.getChildViewHolder(child) instanceof StatusDisplayItem.Holder<?> holder){
							String itemID=holder.getItemID();
							if(ObjectIdComparator.INSTANCE.compare(itemID, nf.unreadMarker)>0){
								parent.getDecoratedBoundsWithMargins(child, tmpRect);
								c.drawRect(tmpRect, paint);
							}
						}
					}
				}
			}
		}, 0);
	}

	@Override
	protected List<View> getViewsForElevationEffect(){
		if (getParentFragment() instanceof NotificationsFragment nf) {
			ArrayList<View> views=new ArrayList<>(super.getViewsForElevationEffect());
			views.add(nf.tabLayout);
			return views;
		} else {
			return super.getViewsForElevationEffect();
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
		outState.putBoolean("onlyMentions", onlyMentions);
		outState.putBoolean("onlyPosts", onlyPosts);
	}

	private Notification getNotificationByID(String id){
		for(Notification n:data){
			if(n.id.equals(id))
				return n;
		}
		return null;
	}

	@Subscribe
	public void onPollUpdated(PollUpdatedEvent ev){
		if(!ev.accountID.equals(accountID))
			return;
		for(Notification ntf:data){
			if(ntf.status==null)
				continue;
			Status contentStatus=ntf.status.getContentStatus();
			if(contentStatus.poll!=null && contentStatus.poll.id.equals(ev.poll.id)){
				updatePoll(ntf.id, ntf.status, ev.poll);
			}
		}
	}

	// copied from StatusListFragment.EventListener (just like the method above)
	// (which assumes this.data to be a list of statuses...)
	@Subscribe
	public void onStatusCountersUpdated(StatusCountersUpdatedEvent ev){
		for(Notification n:data){
			if (n.status == null) continue;
			if(n.status.getContentStatus().id.equals(ev.id)){
				n.status.getContentStatus().update(ev);
				for(int i=0;i<list.getChildCount();i++){
					RecyclerView.ViewHolder holder=list.getChildViewHolder(list.getChildAt(i));
					if(holder instanceof FooterStatusDisplayItem.Holder footer && footer.getItem().status==n.status.getContentStatus()){
						footer.rebind();
					}else if(holder instanceof ExtendedFooterStatusDisplayItem.Holder footer && footer.getItem().status==n.status.getContentStatus()){
						footer.rebind();
					}
				}
			}
		}
		for(Notification n:preloadedData){
			if (n.status == null) continue;
			if(n.status.getContentStatus().id.equals(ev.id)){
				n.status.getContentStatus().update(ev);
			}
		}
	}

	@Subscribe
	public void onRemoveAccountPostsEvent(RemoveAccountPostsEvent ev){
		if(!ev.accountID.equals(accountID) || ev.isUnfollow)
			return;
		List<Notification> toRemove=Stream.concat(data.stream(), preloadedData.stream())
				.filter(n->n.account!=null && n.account.id.equals(ev.postsByAccountID))
				.collect(Collectors.toList());
		for(Notification n:toRemove){
			removeNotification(n);
		}
	}

	public void removeNotification(Notification n){
		data.remove(n);
		preloadedData.remove(n);
		int index=-1;
		for(int i=0;i<displayItems.size();i++){
			if(n.id.equals(displayItems.get(i).parentID)){
				index=i;
				break;
			}
		}
		if(index==-1)
			return;
		int lastIndex;
		for(lastIndex=index;lastIndex<displayItems.size();lastIndex++){
			if(!displayItems.get(lastIndex).parentID.equals(n.id))
				break;
		}
		displayItems.subList(index, lastIndex).clear();
		adapter.notifyItemRangeRemoved(index, lastIndex-index);
	}

	@Override
	protected boolean needDividerForExtraItem(View child, View bottomSibling, RecyclerView.ViewHolder holder, RecyclerView.ViewHolder siblingHolder){
		return super.needDividerForExtraItem(child, bottomSibling, holder, siblingHolder) || (siblingHolder!=null && siblingHolder.getAbsoluteAdapterPosition()>=adapter.getItemCount());
	}

	void resetUnreadBackground(){
		if (getParentFragment() instanceof NotificationsFragment nf) {
			nf.unreadMarker=nf.realUnreadMarker;
			list.invalidate();
		}
	}

	@Override
	public void onRefresh(){
		super.onRefresh();
		resetUnreadBackground();
		if (getParentFragment() instanceof NotificationsFragment nf) {
			AccountSessionManager.get(accountID).reloadNotificationsMarker(m->{
				nf.unreadMarker=nf.realUnreadMarker=m;
			});
		}
	}


	@Override
	public Uri getWebUri(Uri.Builder base) {
		return base.path(isInstanceAkkoma()
				? "/users/" + getSession().self.username + "/interactions"
				: "/notifications").build();
	}
}
