package com.insanitybringer.padsim.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.insanitybringer.padsim.CardEditorActivity;
import com.insanitybringer.padsim.R;
import com.insanitybringer.padsim.TeamEditorActivity;
import com.insanitybringer.padsim.game.AwokenSkill;
import com.insanitybringer.padsim.game.Card;
import com.insanitybringer.padsim.game.LatentAwoken;
import com.insanitybringer.padsim.game.Monster;
import com.insanitybringer.padsim.game.MonsterType;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

public class TeamListAdapter extends RecyclerView.Adapter<TeamListAdapter.ViewHolder>
{
    private int teamid;
    private Card[] team;
    public Resources resources;

    public TeamListAdapter(int id, Card[] team, Resources resources)
    {
        this.teamid = id;
        this.team = team;
        this.resources = resources;
    }
    @NonNull
    @Override
    public TeamListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_editor_entry, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TeamListAdapter.ViewHolder viewHolder, int i)
    {
        Card teammate = team[i];
        viewHolder.teammate = teammate;
        if (teammate == null)
        {
            viewHolder.nameView.setText("None");
            viewHolder.statsView.setText(String.format(Locale.getDefault(), "LV: %d HP: %d ATK: %d RCV: %d", 0, 0, 0, 0));
            BitmapDrawable resource = (BitmapDrawable)resources.getDrawable(R.drawable.blank_card);
            viewHolder.picView.setImageBitmap(resource.getBitmap());
            for (int li = 0; li < 9; li++) viewHolder.awokenViews[li].setVisibility(View.INVISIBLE);
            for (int li = 0; li < 6; li++) viewHolder.latentViews[li].setVisibility(View.INVISIBLE);
            for (int li = 0; li < 3; li++) viewHolder.typeViews[li].setVisibility(View.INVISIBLE);
        }
        else
        {
            viewHolder.nameView.setText(teammate.getMonster().getIDName());
            viewHolder.statsView.setText(String.format(Locale.getDefault(), "LV: %d HP: %d ATK: %d RCV: %d", teammate.getLevel(), teammate.getHealth(), teammate.getAttack(), teammate.getRecovery()));
            ImageLoaderTask task = new ImageLoaderTask(i, resources, viewHolder);

            if (teammate.getInheritCard().getID() != 0)
            {
                ImageLoaderTask assistTask = new ImageLoaderTask(i, resources, viewHolder, true);
                assistTask.execute(teammate.getInheritCard().getID());
            }
            else
            {
                viewHolder.assistPicView.setVisibility(View.GONE);
            }

            int awokenid, typeid;
            BitmapDrawable resource;
            for (int j = 0; j < 9; j++)
            {
                awokenid = teammate.getMonster().getAwokenSkills()[j];
                if (awokenid == 0)
                {
                    viewHolder.awokenViews[j].setVisibility(View.INVISIBLE);
                } else
                {
                    resource = (BitmapDrawable) ResourcesCompat.getDrawable(resources, AwokenSkill.resourceids[awokenid], null);
                    viewHolder.awokenViews[j].setVisibility(View.VISIBLE);
                    viewHolder.awokenViews[j].setImageBitmap(resource.getBitmap());
                    viewHolder.awokenViews[j].setAlpha(0.3f);
                    if (j < teammate.getAwokenLevel())
                        viewHolder.awokenViews[j].setAlpha(1.0f);
                }
            }
            for (int j = 0; j < 6; j++)
            {
                awokenid = teammate.getLatent(j);
                if (awokenid == 0 || awokenid == LatentAwoken.LATENT_PLACEHOLDER)
                {
                    viewHolder.latentViews[j].setVisibility(View.GONE);
                } else
                {
                    resource = (BitmapDrawable) ResourcesCompat.getDrawable(resources, LatentAwoken.resourceids[awokenid], null);
                    viewHolder.latentViews[j].setVisibility(View.VISIBLE);
                    viewHolder.latentViews[j].setImageBitmap(resource.getBitmap());
                }
            }
            for (int j = 0; j < 3; j++)
            {
                typeid = teammate.getMonster().getType(j);
                if (typeid == -1)
                {
                    viewHolder.typeViews[j].setVisibility(View.GONE);
                } else
                {
                    resource = (BitmapDrawable) ResourcesCompat.getDrawable(resources, MonsterType.resourceids[typeid], null);
                    viewHolder.typeViews[j].setVisibility(View.VISIBLE);
                    viewHolder.typeViews[j].setImageBitmap(resource.getBitmap());
                }
            }
            task.execute(teammate.getMonster().getId());
        }
    }

    @Override
    public int getItemCount()
    {
        return 6;
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public ImageView picView;
        public ImageView assistPicView;
        public TextView nameView;
        public TextView statsView;
        public ImageView[] awokenViews = new ImageView[9];
        public ImageView[] latentViews = new ImageView[6];
        public ImageView[] typeViews = new ImageView[3];
        public Card teammate;
        //public int id;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            picView = itemView.findViewById(R.id.teammate_image_view);
            assistPicView = itemView.findViewById(R.id.assist_image_view);
            nameView = itemView.findViewById(R.id.teammate_name);
            statsView = itemView.findViewById(R.id.teammate_stats);
            awokenViews[0] = itemView.findViewById(R.id.awoken1);
            awokenViews[1] = itemView.findViewById(R.id.awoken2);
            awokenViews[2] = itemView.findViewById(R.id.awoken3);
            awokenViews[3] = itemView.findViewById(R.id.awoken4);
            awokenViews[4] = itemView.findViewById(R.id.awoken5);
            awokenViews[5] = itemView.findViewById(R.id.awoken6);
            awokenViews[6] = itemView.findViewById(R.id.awoken7);
            awokenViews[7] = itemView.findViewById(R.id.awoken8);
            awokenViews[8] = itemView.findViewById(R.id.awoken9);
            latentViews[0] = itemView.findViewById(R.id.latent1);
            latentViews[1] = itemView.findViewById(R.id.latent2);
            latentViews[2] = itemView.findViewById(R.id.latent3);
            latentViews[3] = itemView.findViewById(R.id.latent4);
            latentViews[4] = itemView.findViewById(R.id.latent5);
            latentViews[5] = itemView.findViewById(R.id.latent6);
            typeViews[0] = itemView.findViewById(R.id.type1);
            typeViews[1] = itemView.findViewById(R.id.type2);
            typeViews[2] = itemView.findViewById(R.id.type3);

            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    //System.out.println("Element " + getAdapterPosition() + " clicked. My context is " + v.getContext().getClass().getCanonicalName());
                    if (v.getContext() instanceof Activity)
                    {
                        Intent intent = new Intent(v.getContext(), CardEditorActivity.class);
                        intent.putExtra(TeamEditorActivity.MSG_TEAMMATE, getLayoutPosition());
                        intent.putExtra(TeamEditorActivity.MSG_TEAMNUM, teamid);
                        //v.getContext().startActivity(intent);
                        ((Activity) v.getContext()).startActivityForResult(intent, teamid);
                    }
                }
            });
        }
    }

    private static class ImageLoaderTask extends AsyncTask<Integer, Integer, Bitmap>
    {
        private ViewHolder holder;
        private Resources resources;
        private int position;
        private boolean assist = false;
        public ImageLoaderTask(int position, Resources resources, ViewHolder holder)
        {
            this.position = position;
            this.resources = resources;
            this.holder = holder;
        }

        public ImageLoaderTask(int position, Resources resources, ViewHolder holder, boolean assist)
        {
            this.position = position;
            this.resources = resources;
            this.holder = holder;
            this.assist = true;
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
                    if (assist)
                    {
                        holder.assistPicView.setVisibility(View.VISIBLE);
                        holder.assistPicView.setImageBitmap(result);
                    }
                    else
                    {
                        holder.picView.setVisibility(View.VISIBLE);
                        holder.picView.setImageBitmap(result);
                    }
                }
            }
        }
    }
}
