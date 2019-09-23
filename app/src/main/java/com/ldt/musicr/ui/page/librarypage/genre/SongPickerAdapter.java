package com.ldt.musicr.ui.page.librarypage.genre;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.ldt.musicr.R;
import com.ldt.musicr.model.Song;
import com.ldt.musicr.ui.widget.bubblepicker.model.BubbleGradient;
import com.ldt.musicr.ui.widget.bubblepicker.model.PickerItem;
import com.ldt.musicr.ui.widget.bubblepicker.rendering.PickerAdapter;
import com.ldt.musicr.util.MusicUtil;

public class SongPickerAdapter extends PickerAdapter<Song> {

    public SongPickerAdapter(Context context) {
        super(context);
    }

    @Override
    public boolean onBindItem(PickerItem item, boolean create, int i) {
        super.onBindItem(item,create,i);
        Song song = mData.get(i);
        item.setTitle(song.title);
        item.setIconSizeUnit(PickerItem.SIZE_RANDOM);
        // Glide
        Glide.with(mContext).load(MusicUtil.getMediaStoreAlbumCoverUri(song.albumId)).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                item.setBackgroundImage(resource);
                SongPickerAdapter.this.notifyBackImageUpdated(i);
            }
        });

        return true;
    }
}
