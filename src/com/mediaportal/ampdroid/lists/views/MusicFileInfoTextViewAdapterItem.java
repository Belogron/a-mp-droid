package com.mediaportal.ampdroid.lists.views;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

import com.mediaportal.ampdroid.R;
import com.mediaportal.ampdroid.data.FileInfo;
import com.mediaportal.ampdroid.lists.ILoadingAdapterItem;
import com.mediaportal.ampdroid.lists.LazyLoadingImage;
import com.mediaportal.ampdroid.lists.SubtextViewHolder;
import com.mediaportal.ampdroid.lists.ViewHolder;

public class MusicFileInfoTextViewAdapterItem implements ILoadingAdapterItem {
   private FileInfo mTracks;
   private String mSection;
   public MusicFileInfoTextViewAdapterItem(FileInfo _track) {
      super();
      mTracks = _track;
      
      String prettyName = mTracks.getName();
      if(prettyName != null && prettyName.length() > 0){
         String firstLetter = prettyName.substring(0, 1);
         mSection = firstLetter.toUpperCase();
      }
   }
   @Override
   public LazyLoadingImage getImage() {
      return null;
   }

   @Override
   public int getLoadingImageResource() {
      return 0;
   }

   @Override
   public int getDefaultImageResource() {
      return 0;
   }

   @Override
   public int getType() {
      return 0;
   }

   @Override
   public int getXml() {
      return R.layout.listitem_text;
   }

   @Override
   public Object getItem() {
      return mTracks;
   }

   @Override
   public ViewHolder createViewHolder(View _view) {
      SubtextViewHolder holder = new SubtextViewHolder();
      holder.text = (TextView) _view.findViewById(R.id.TextViewText);
      return holder;
   }

   @Override
   public void fillViewFromViewHolder(ViewHolder _holder) {
      SubtextViewHolder holder = (SubtextViewHolder)_holder;

      if (holder.text != null) {
         holder.text.setText(mTracks.getName());
         holder.text.setTextColor(Color.WHITE);
         if(mTracks.isFolder()){
            holder.text.setTypeface(null, Typeface.BOLD);
         }
         else{
            holder.text.setTypeface(null, Typeface.NORMAL);
         }
      }
   }

   @Override
   public String getSection() {
      return mSection;
   }
}
