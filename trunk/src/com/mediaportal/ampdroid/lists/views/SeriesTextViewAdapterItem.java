package com.mediaportal.ampdroid.lists.views;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.mediaportal.ampdroid.R;
import com.mediaportal.ampdroid.data.Series;
import com.mediaportal.ampdroid.lists.ILoadingAdapterItem;
import com.mediaportal.ampdroid.lists.LazyLoadingAdapter.ViewHolder;
import com.mediaportal.ampdroid.lists.LazyLoadingImage;
import com.mediaportal.ampdroid.lists.SubtextViewHolder;

public class SeriesTextViewAdapterItem implements ILoadingAdapterItem {
   private Series mSeries;
   private String mSection;
   public SeriesTextViewAdapterItem(Series _series) {
      super();
      this.mSeries = _series;
      
      String prettyName = mSeries.getPrettyName();
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
      return mSeries;
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
         holder.text.setText(mSeries.getPrettyName());
         holder.text.setTextColor(Color.WHITE);
      }
   }
   @Override
   public String getSection() {
      return mSection;
   }
}
