package org.joinmastodon.android.ui.displayitems;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.joinmastodon.android.R;
import org.joinmastodon.android.api.MastodonAPIRequest;
import org.joinmastodon.android.api.requests.statuses.PleromaAddStatusReaction;
import org.joinmastodon.android.api.requests.statuses.PleromaDeleteStatusReaction;
import org.joinmastodon.android.fragments.BaseStatusListFragment;
import org.joinmastodon.android.fragments.account_list.StatusEmojiReactionsListFragment;
import org.joinmastodon.android.model.Emoji;
import org.joinmastodon.android.model.EmojiReaction;
import org.joinmastodon.android.model.Status;
import org.joinmastodon.android.ui.EmojiReactionsView;
import org.joinmastodon.android.ui.text.HtmlParser;
import org.joinmastodon.android.ui.utils.CustomEmojiHelper;
import org.joinmastodon.android.ui.utils.UiUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import me.grishka.appkit.Nav;
import me.grishka.appkit.api.Callback;
import me.grishka.appkit.api.ErrorResponse;
import me.grishka.appkit.imageloader.ImageLoaderViewHolder;
import me.grishka.appkit.imageloader.requests.ImageLoaderRequest;
import me.grishka.appkit.utils.V;

public class EmojiReactionsStatusDisplayItem extends StatusDisplayItem {
    public final Status status;
    public final Map<String, EmojiReactionDisplay> reactions = new LinkedHashMap<>();

    private final CustomEmojiHelper emojiHelper=new CustomEmojiHelper();

    public EmojiReactionsStatusDisplayItem(String parentID, BaseStatusListFragment parentFragment, Status status) {
        super(parentID, parentFragment);
        this.status = status;
        if (status.emojiReactions != null){
			for(EmojiReaction emojiReaction : status.emojiReactions){
                reactions.put(emojiReaction.name, createDisplay(emojiReaction, true));
			}
        }
    }

    public EmojiReactionDisplay createDisplay(EmojiReaction emojiReaction, boolean addToHelper) {
        int atSymbolIndex = emojiReaction.name.indexOf("@");
        String name = emojiReaction.name.substring(0, atSymbolIndex != -1 ? atSymbolIndex : emojiReaction.name.length());
        SpannableStringBuilder ssb = new SpannableStringBuilder(emojiReaction.url != null ? ":" + name + ":" : name);
        ssb.append("\n").append(String.valueOf(emojiReaction.count));
        int countStartIndex = ssb.length() - String.valueOf(emojiReaction.count).length();
        ssb.setSpan(new RelativeSizeSpan(1.4f), 0, countStartIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssb.setSpan(new RelativeSizeSpan(0.8f), countStartIndex, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (emojiReaction.url != null) {
            Emoji emoji = new Emoji();
            emoji.shortcode = name;
            emoji.url = emojiReaction.url;
            HtmlParser.parseCustomEmoji(ssb, Collections.singletonList(emoji));
            if(addToHelper)
                emojiHelper.addText(ssb);
        }
        return new EmojiReactionDisplay(ssb, emojiReaction);
    }

    @Override
    public Type getType(){
        return Type.EMOJI_REACTIONS;
    }

    @Override
    public int getImageCount(){
        return emojiHelper.getImageCount();
    }

    @Override
    public ImageLoaderRequest getImageRequest(int index){
        return emojiHelper.getImageRequest(index);
    }

    public static class Holder extends StatusDisplayItem.Holder<EmojiReactionsStatusDisplayItem> implements ImageLoaderViewHolder {
        private final EmojiReactionsView grid;
        private final List<Button> buttons = new ArrayList<>();
        private ArrayAdapter<Button> adapter;

        public Holder(Activity activity, ViewGroup parent) {
            super(activity, R.layout.display_item_emoji_reactions, parent);
            this.grid = findViewById(R.id.reaction_layout);
        }

        @Override
        public void onBind(EmojiReactionsStatusDisplayItem item) {
            buttons.clear();

            for (EmojiReactionDisplay reaction : item.reactions.values()) {
                Button btn = new Button(item.parentFragment.getContext());
                btn.setHeight(V.dp(48));
                btn.setPaddingRelative(V.dp(12), 0, V.dp(12), 0);
                btn.setBackgroundResource(R.drawable.bg_button_m3_tonal);
                btn.setTextColor(item.parentFragment.getContext().getColor(R.color.gray_800));
                btn.setText(reaction.text);
                if (reaction.reaction.me)
                    btn.getBackground()
                            .setColorFilter(UiUtils.getThemeColor(item.parentFragment.getContext(), R.attr.colorM3SecondaryContainer), PorterDuff.Mode.SRC);

                buttons.add(btn);

                btn.setOnClickListener(e -> {
                    EmojiReaction emojiReaction = item.reactions.get(reaction.reaction.name).reaction;
                    MastodonAPIRequest<Status> req =
                            emojiReaction.me ? new PleromaDeleteStatusReaction(item.status.id, emojiReaction.name)
                                    : new PleromaAddStatusReaction(item.status.id, emojiReaction.name);
                    req.setCallback(new Callback<>() {
                        @Override
                        public void onSuccess(Status result) {
                            Optional<EmojiReaction> newReaction =
                                    result.emojiReactions.stream().filter(v -> v.name.equals(emojiReaction.name)).findFirst();
                            if (newReaction.isPresent()) {
                                EmojiReactionDisplay newReactionDisplay = item.createDisplay(newReaction.get(), false);
                                item.reactions.put(emojiReaction.name, newReactionDisplay);
                                btn.setText(newReactionDisplay.text);

                                if (newReactionDisplay.reaction.me)
                                    btn.getBackground()
                                            .setColorFilter(UiUtils.getThemeColor(item.parentFragment.getContext(), R.attr.colorM3SecondaryContainer), PorterDuff.Mode.SRC);
                                else
                                    btn.getBackground().clearColorFilter();

                                UiUtils.loadCustomEmojiInTextView(btn);
                            } else {
                                adapter.remove(btn);
                            }
                        }

                        @Override
                        public void onError(ErrorResponse error) {
                        }
                    })
                    .exec(item.parentFragment.getAccountID());
                });

                btn.setOnLongClickListener(e -> {
                    EmojiReaction emojiReaction = item.reactions.get(reaction.reaction.name).reaction;
                    Bundle args=new Bundle();
                    args.putString("account", item.parentFragment.getAccountID());
                    args.putString("statusID", item.status.id);
                    int atSymbolIndex = emojiReaction.name.indexOf("@");
                    args.putString("emoji", atSymbolIndex != -1 ? emojiReaction.name.substring(0, atSymbolIndex) : emojiReaction.name);
                    args.putString("url", emojiReaction.url);
                    args.putInt("count", emojiReaction.count);
                    Nav.go(item.parentFragment.getActivity(), StatusEmojiReactionsListFragment.class, args);
                    return true;
                });

                UiUtils.loadCustomEmojiInTextView(btn);
            }

            adapter = new ArrayAdapter<>(item.parentFragment.getContext(), 0, buttons) {
                public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                    return getItem(position);
                }
            };
            grid.setAdapter(adapter);
        }

        @Override
        public void setImage(int index, Drawable drawable){
            item.emojiHelper.setImageDrawable(index, drawable);
            buttons.get(index).invalidate();
            if(drawable instanceof Animatable)
                ((Animatable) drawable).start();
        }

        @Override
        public void clearImage(int index){
            setImage(index, null);
        }
    }

    private static class EmojiReactionDisplay {
        public SpannableStringBuilder text;
        public EmojiReaction reaction;

        public EmojiReactionDisplay(SpannableStringBuilder text, EmojiReaction reaction) {
            this.text = text;
            this.reaction = reaction;
        }
    }
}