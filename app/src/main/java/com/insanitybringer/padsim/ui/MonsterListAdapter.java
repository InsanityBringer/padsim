package com.insanitybringer.padsim.ui;

import android.app.Activity;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.util.ListUpdateCallback;
import android.support.v7.widget.RecyclerView;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.insanitybringer.padsim.R;
import com.insanitybringer.padsim.game.Monster;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

public class MonsterListAdapter extends RecyclerView.Adapter<MonsterListAdapter.ViewHolder>
{
    private static final int CACHE_SIZE = 4 * 1024 * 1024; //4 MB of images, should be quite a few thumbnails
    private List<Monster> monsters;
    private LruCache<Integer, Bitmap> bitmapCache;
    public Resources resources;

    public MonsterListAdapter(List<Monster> monsters, Resources resources)
    {
        this.monsters = monsters;
        this.resources = resources;
        this.bitmapCache = new LruCache<Integer, Bitmap>(CACHE_SIZE) {
            protected int sizeOf(Integer key, Bitmap value) {
                return value.getByteCount();
            }
        };
        setHasStableIds(true);
    }

    /*public List<Monster> getMonsters()
    {
        return monsters;
    }*/

    //I HATE THIS CODE AAAAAAAAAAAAAAAAAAAA
    //Changes the list to a given list of monsters, animating as needed.
    public void updateList(List<Monster> newMonsters)
    {
        //Should probably run in separate thread, since blocks UI for a while
        ListChangeCallback callback = new ListChangeCallback(monsters, newMonsters);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(callback);
        this.monsters = newMonsters;
        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public MonsterListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_list_entry, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder parent, int i)
    {
        //parent.id = i;
        //parent.nameView.setText(String.format("%d: %s", monsters.get(i).getId(), monsters.get(i).getName()));
        //Update the inflated views for this particular entry
        parent.id = monsters.get(i).getId();
        parent.nameView.setText(monsters.get(i).getIDName());
        parent.statsView.setText(String.format("HP: %d ATK: %d RCV: %d", monsters.get(i).getMaxHealth(), monsters.get(i).getMaxAttack(), monsters.get(i).getMaxRecovery()));
        ImageLoaderTask task = new ImageLoaderTask(i, resources, parent);
        task.execute(monsters.get(i).getId());
    }

    @Override
    public long getItemId(int position)
    {
        return monsters.get(position).getId();
    }

    @Override
    public int getItemCount()
    {
        return monsters.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public ImageView picView;
        public TextView nameView;
        public TextView statsView;
        public int id;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            picView = itemView.findViewById(R.id.monsterIcon);
            nameView = itemView.findViewById(R.id.monsterName);
            statsView = itemView.findViewById(R.id.monsterStats);
            //Simple tap handling
            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    //System.out.println("Element " + getAdapterPosition() + " clicked. Root view is" + v.getRootView().getClass().getCanonicalName());
                    if (v.getContext() instanceof Activity)
                    {
                        Activity act = (Activity)v.getContext();
                        act.setResult(id);
                        act.finish();
                    }
                }
            });
        }
    }

    private static class ListChangeCallback extends DiffUtil.Callback
    {
        private List<Monster> list1, list2;
        public ListChangeCallback(List<Monster> l1, List<Monster> l2)
        {
            list1 = l1; list2 = l2;
        }
        @Override
        public int getOldListSize()
        {
            return list1.size();
        }

        @Override
        public int getNewListSize()
        {
            return list2.size();
        }

        @Override
        public boolean areItemsTheSame(int i, int i1)
        {
            return list1.get(i).getId() == list2.get(i1).getId();
        }

        @Override
        public boolean areContentsTheSame(int i, int i1)
        {
            return list1.get(i) == list2.get(i1);
        }

        @Nullable
        @Override
        public Object getChangePayload(int oldItemPosition, int newItemPosition)
        {
            return super.getChangePayload(oldItemPosition, newItemPosition);
        }
    }


    private static class ImageLoaderTask extends AsyncTask<Integer, Integer, Bitmap>
    {
        private ViewHolder holder;
        private Resources resources;
        private int position;
        public ImageLoaderTask(int position, Resources resources, ViewHolder holder)
        {
            this.position = position;
            this.resources = resources;
            this.holder = holder;
        }

        @Override
        protected Bitmap doInBackground(Integer... integers)
        {
            AssetManager assets = resources.getAssets();
            try
            {
                InputStream bitmapStream = assets.open(String.format(Locale.getDefault(), "icon/%04d.png", integers[0]));
                BitmapDrawable bitmapDrawable = new BitmapDrawable(resources, bitmapStream);
                return bitmapDrawable.getBitmap();
            } catch (IOException e)
            {
                System.out.println("ICON LOADING ERROR this shouldn't be happening bug IB");
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap result)
        {
            super.onPostExecute(result);
            if (result != null)
            {
                if (holder.getLayoutPosition() == position)
                {
                    holder.picView.setVisibility(View.VISIBLE);
                    holder.picView.setImageBitmap(result);
                }
            }
        }
    }
}
