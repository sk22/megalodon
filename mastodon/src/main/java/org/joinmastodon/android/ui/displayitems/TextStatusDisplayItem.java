package org.joinmastodon.android.ui.displayitems;

import android.app.Activity;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import org.joinmastodon.android.GlobalUserPreferences;
import org.joinmastodon.android.R;
import org.joinmastodon.android.api.session.AccountSessionManager;
import org.joinmastodon.android.fragments.BaseStatusListFragment;
import org.joinmastodon.android.model.Status;
import org.joinmastodon.android.model.Translation;
import org.joinmastodon.android.ui.text.HtmlParser;
import org.joinmastodon.android.ui.utils.CustomEmojiHelper;
import org.joinmastodon.android.ui.utils.UiUtils;
import org.joinmastodon.android.ui.views.LinkedTextView;

import java.util.Locale;

import me.grishka.appkit.imageloader.ImageLoaderViewHolder;
import me.grishka.appkit.imageloader.MovieDrawable;
import me.grishka.appkit.imageloader.requests.ImageLoaderRequest;
import me.grishka.appkit.utils.CubicBezierInterpolator;
import me.grishka.appkit.utils.V;

public class TextStatusDisplayItem extends StatusDisplayItem{
	private CharSequence text;
	private CustomEmojiHelper emojiHelper=new CustomEmojiHelper();
	private CharSequence translatedText;
	private CustomEmojiHelper translationEmojiHelper=new CustomEmojiHelper();
	public boolean textSelectable;
	public boolean reduceTopPadding;
	public boolean disableTranslate;

	public TextStatusDisplayItem(String parentID, CharSequence text, BaseStatusListFragment parentFragment, Status status, boolean disableTranslate){
		super(parentID, parentFragment);
		this.text=text;
		this.status=status;
		this.disableTranslate=disableTranslate;
		emojiHelper.setText(text);
	}

	@Override
	public Type getType(){
		return Type.TEXT;
	}

	@Override
	public int getImageCount(){
		return getCurrentEmojiHelper().getImageCount();
	}

	@Override
	public ImageLoaderRequest getImageRequest(int index){
		return getCurrentEmojiHelper().getImageRequest(index);
	}

	public void setTranslatedText(String text){
		Status statusForContent=status.getContentStatus();
		translatedText=HtmlParser.parse(text, statusForContent.emojis, statusForContent.mentions, statusForContent.tags, parentFragment.getAccountID());
		translationEmojiHelper.setText(translatedText);
	}

	private CustomEmojiHelper getCurrentEmojiHelper(){
		return status.translationState==Status.TranslationState.SHOWN ? translationEmojiHelper : emojiHelper;
	}

	public static class Holder extends StatusDisplayItem.Holder<TextStatusDisplayItem> implements ImageLoaderViewHolder{
		private final LinkedTextView text;
		private final ViewStub translationFooterStub;
		private View translationFooter, translationButtonWrap;
		private TextView translationInfo;
		private Button translationButton;
		private ProgressBar translationProgress;

		private final float textMaxHeight;
		private final LinearLayout.LayoutParams collapseParams, wrapParams;
		private final ViewGroup parent;
		private final TextView readMore;
		private final ScrollView textScrollView;

		public Holder(Activity activity, ViewGroup parent){
			super(activity, R.layout.display_item_text, parent);
			this.parent=parent;
			text=findViewById(R.id.text);
			translationFooterStub=findViewById(R.id.translation_info);
			textScrollView=findViewById(R.id.text_scroll_view);
			readMore=findViewById(R.id.read_more);
			textMaxHeight=activity.getResources().getDimension(R.dimen.text_max_height);
			float textCollapsedHeight=activity.getResources().getDimension(R.dimen.text_collapsed_height);
			collapseParams=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) textCollapsedHeight);
			wrapParams=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			readMore.setOnClickListener(v -> item.parentFragment.onToggleExpanded(item.status, item.isForQuote, getItemID()));
		}

		@Override
		public void onBind(TextStatusDisplayItem item){
			if(item.status.translationState==Status.TranslationState.SHOWN){
				if(item.translatedText==null){
					item.setTranslatedText(item.status.translation.content);
				}
				text.setText(item.translatedText);
			}else{
				text.setText(item.text);
			}
			text.setTextIsSelectable(false);
			if(item.textSelectable && !item.isForQuote) itemView.post(() -> text.setTextIsSelectable(true));
			text.setInvalidateOnEveryFrame(false);
			itemView.setClickable(false);
			itemView.setPadding(itemView.getPaddingLeft(), item.reduceTopPadding ? V.dp(6) : V.dp(12), itemView.getPaddingRight(), itemView.getPaddingBottom());
			text.setTextColor(UiUtils.getThemeColor(text.getContext(), item.inset ? R.attr.colorM3OnSurfaceVariant : R.attr.colorM3OnSurface));
			updateTranslation(false);

			readMore.setText(item.status.textExpanded ? R.string.sk_collapse : R.string.sk_expand);

			StatusDisplayItem next=getNextVisibleDisplayItem().orElse(null);
			if(next!=null && !next.parentID.equals(item.parentID)) next=null;
			int bottomPadding=item.inset ? V.dp(12)
					: next instanceof FooterStatusDisplayItem ? V.dp(6)
					: (next instanceof EmojiReactionsStatusDisplayItem || next==null) ? 0
					: V.dp(12);
			itemView.setPadding(itemView.getPaddingLeft(), itemView.getPaddingTop(), itemView.getPaddingRight(), bottomPadding);

			if (!GlobalUserPreferences.collapseLongPosts) {
				textScrollView.setLayoutParams(wrapParams);
				readMore.setVisibility(View.GONE);
			}

			// incredibly ugly workaround for https://github.com/sk22/megalodon/issues/520
			// i am so, so sorry. FIXME
			// attempts to use OnPreDrawListener, OnGlobalLayoutListener and .post have failed -
			// the view didn't want to reliably update after calling .setVisibility etc :(
			int width = parent.getWidth() != 0 ? parent.getWidth()
					: item.parentFragment.getView().getWidth() != 0
					? item.parentFragment.getView().getWidth()
					: item.parentFragment.getParentFragment() != null && item.parentFragment.getParentFragment().getView().getWidth() != 0
					? item.parentFragment.getParentFragment().getView().getWidth() // YIKES
					: UiUtils.MAX_WIDTH;

			text.measure(
					View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
					View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

			if (GlobalUserPreferences.collapseLongPosts && !item.status.textExpandable) {
				boolean tooBig = text.getMeasuredHeight() > textMaxHeight;
				boolean expandable = tooBig && !item.status.hasSpoiler();
				item.parentFragment.onEnableExpandable(Holder.this, expandable, item.isForQuote);
			}

			boolean expandButtonShown=item.status.textExpandable && !item.status.textExpanded;
			if(translationFooter!=null)
				translationFooter.setPadding(0, V.dp(expandButtonShown ? 0 : 4), 0, 0);
			readMore.setVisibility(expandButtonShown ? View.VISIBLE : View.GONE);
			textScrollView.setLayoutParams(item.status.textExpandable && !item.status.textExpanded ? collapseParams : wrapParams);

			// compensate for spoiler's bottom margin
			ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) itemView.getLayoutParams();
			params.setMargins(params.leftMargin, item.inset && item.status.hasSpoiler() ? V.dp(-16) : 0,
					params.rightMargin, params.bottomMargin);
		}

		@Override
		public void setImage(int index, Drawable image){
			getEmojiHelper().setImageDrawable(index, image);
			text.invalidate();
			if(image instanceof Animatable){
				((Animatable) image).start();
				if(image instanceof MovieDrawable)
					text.setInvalidateOnEveryFrame(true);
			}
		}

		@Override
		public void clearImage(int index){
			getEmojiHelper().setImageDrawable(index, null);
			text.invalidate();
		}

		private CustomEmojiHelper getEmojiHelper(){
			return item.emojiHelper;
		}

		public void updateTranslation(boolean updateText){
			if(item.status==null)
				return;
			boolean translateEnabled=!item.disableTranslate && item.status.isEligibleForTranslation(item.parentFragment.getSession()) && !item.isForQuote;
			if(translationFooter==null && translateEnabled){
				translationFooter=translationFooterStub.inflate();
				translationInfo=findViewById(R.id.translation_info_text);
				translationButton=findViewById(R.id.translation_btn);
				translationButtonWrap=findViewById(R.id.translation_btn_wrap);
				translationProgress=findViewById(R.id.translation_progress);
				translationButton.setOnClickListener(v->item.parentFragment.togglePostTranslation(item.status, item.parentID));
			}
			if(translationButton!=null) translationButton.animate().cancel();
			if(item.status.translationState==Status.TranslationState.HIDDEN){
				if(updateText) text.setText(item.text);
				if(translationFooter==null) return;
				translationFooter.setVisibility(translateEnabled ? View.VISIBLE : View.GONE);
				translationProgress.setVisibility(View.GONE);
				Translation existingTrans=item.status.getContentStatus().translation;
				String existingTransLang=existingTrans!=null ? existingTrans.detectedSourceLanguage : null;
				String lang=existingTransLang!=null ? existingTransLang : item.status.getContentStatus().language;
				Locale locale=lang!=null ? Locale.forLanguageTag(lang) : null;
				String displayLang=locale==null || locale.getDisplayLanguage().isBlank() ? lang : locale.getDisplayLanguage();
				translationButton.setText(displayLang!=null
						? item.parentFragment.getString(R.string.translate_post, displayLang)
						: item.parentFragment.getString(R.string.sk_translate_post));
				translationButton.setClickable(true);
				translationButton.animate().alpha(1).setDuration(100).start();
				translationInfo.setVisibility(View.GONE);
				UiUtils.beginLayoutTransition((ViewGroup) translationButtonWrap);
			}else{
				translationFooter.setVisibility(View.VISIBLE);
				if(item.status.translationState==Status.TranslationState.SHOWN){
					translationProgress.setVisibility(View.GONE);
					translationButton.setText(R.string.translation_show_original);
					translationButton.setClickable(true);
					translationButton.animate().alpha(1).setDuration(200).start();
					translationInfo.setVisibility(View.VISIBLE);
					translationButton.setVisibility(View.VISIBLE);
					String displayLang=Locale.forLanguageTag(item.status.translation.detectedSourceLanguage).getDisplayLanguage();
					translationInfo.setText(translationInfo.getContext().getString(R.string.post_translated, !displayLang.isBlank() ? displayLang : item.status.translation.detectedSourceLanguage, item.status.translation.provider));
					UiUtils.beginLayoutTransition((ViewGroup) translationButtonWrap);
					if(updateText){
						if(item.translatedText==null){
							item.setTranslatedText(item.status.translation.content);
						}
						text.setText(item.translatedText);
					}
				}else{ // LOADING
					translationProgress.setVisibility(View.VISIBLE);
					translationButton.setClickable(false);
					translationButton.animate().alpha(UiUtils.ALPHA_PRESSED).setStartDelay(50).setDuration(300).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
					translationInfo.setVisibility(View.INVISIBLE);
					UiUtils.beginLayoutTransition((ViewGroup) translationButton.getParent());
				}
			}
		}
	}
}
