package com.insanitybringer.padsim;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.insanitybringer.padsim.game.AwokenSkill;
import com.insanitybringer.padsim.game.Card;
import com.insanitybringer.padsim.game.GameState;
import com.insanitybringer.padsim.game.LatentAwoken;
import com.insanitybringer.padsim.game.Monster;
import com.insanitybringer.padsim.game.MonsterType;
import com.insanitybringer.padsim.game.Skill;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

public class CardEditorActivity extends AppCompatActivity
{
    public final static String PARAM_LISTMODE = "com.insanitybringer.padsim.LISTMODE";

    //I HAVE TOO MANY VIEWS AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
    private ImageView icon;
    private ImageView assistIcon;
    private TextView hpView;
    private TextView atkView;
    private TextView rcvView;
    private EditText hpPlusView;
    private EditText atkPlusView;
    private EditText rcvPlusView;
    private EditText skillLevelView;
    private EditText assistSkillLevelView;
    private ImageView[] awokenViews = new ImageView[10];
    private ImageView[] assistAwokenViews = new ImageView[9];
    private ImageView[] superAwokenViews = new ImageView[9];
    private ImageView[] typeViews = new ImageView[3];
    private ImageView[] latentViews = new ImageView[6];
    private TextView superAwokenLabel;
    private TextView activeName;
    private TextView activeDesc;
    private TextView leaderName;
    private TextView leaderDesc;
    private TextView skillMaxLevel;
    private TextView skillTurns;
    private TextView skillLabel;
    private TextView skillMaxLabel;
    private TextView skillTurnsLabel;
    private EditText assistLevelView;
    private CheckBox assistIs297;
    private TextView assistActiveName;
    private TextView assistActiveDesc;
    private TextView assistSkillMaxLevel;
    private TextView assistSkillTurns;
    private TextView assistSkillLabel;
    private TextView assistSkillMaxLabel;
    private TextView assistSkillTurnsLabel;
    private int teamid = 0;
    private int teammateid = 0;
    private int monsterid = 0;
    private int assistid = 0;
    private GameState gameState;
    private Card card;
    private EditText levelView;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_editor);
        Toolbar appbar = findViewById(R.id.toolbar2);
        setSupportActionBar(appbar);

        icon = findViewById(R.id.card_thumbnail);
        hpView = findViewById(R.id.card_hp);
        atkView = findViewById(R.id.card_atk);
        rcvView = findViewById(R.id.card_rcv);
        hpPlusView = findViewById(R.id.card_hp_plus);
        atkPlusView = findViewById(R.id.card_atk_plus);
        rcvPlusView = findViewById(R.id.card_rcv_plus);
        skillLevelView = findViewById(R.id.card_skill_level);
        assistSkillLevelView = findViewById(R.id.card_skill_level_inherit);
        //code i absolutely hate: this
        awokenViews[0] = findViewById(R.id.card_awoken0); //This should be a loop but ehh
        awokenViews[1] = findViewById(R.id.card_awoken1);
        awokenViews[2] = findViewById(R.id.card_awoken2);
        awokenViews[3] = findViewById(R.id.card_awoken3);
        awokenViews[4] = findViewById(R.id.card_awoken4);
        awokenViews[5] = findViewById(R.id.card_awoken5);
        awokenViews[6] = findViewById(R.id.card_awoken6);
        awokenViews[7] = findViewById(R.id.card_awoken7);
        awokenViews[8] = findViewById(R.id.card_awoken8);
        awokenViews[9] = findViewById(R.id.card_awoken9);
        assistAwokenViews[0] = findViewById(R.id.card_equip1); //This should be a loop but ehh
        assistAwokenViews[1] = findViewById(R.id.card_equip2);
        assistAwokenViews[2] = findViewById(R.id.card_equip3);
        assistAwokenViews[3] = findViewById(R.id.card_equip4);
        assistAwokenViews[4] = findViewById(R.id.card_equip5);
        assistAwokenViews[5] = findViewById(R.id.card_equip6);
        assistAwokenViews[6] = findViewById(R.id.card_equip7);
        assistAwokenViews[7] = findViewById(R.id.card_equip8);
        assistAwokenViews[8] = findViewById(R.id.card_equip9);
        superAwokenViews[0] = findViewById(R.id.card_sa1);
        superAwokenViews[1] = findViewById(R.id.card_sa2);
        superAwokenViews[2] = findViewById(R.id.card_sa3);
        superAwokenViews[3] = findViewById(R.id.card_sa4);
        superAwokenViews[4] = findViewById(R.id.card_sa5);
        superAwokenViews[5] = findViewById(R.id.card_sa6);
        superAwokenViews[6] = findViewById(R.id.card_sa7);
        superAwokenViews[7] = findViewById(R.id.card_sa8);
        superAwokenViews[8] = findViewById(R.id.card_sa9);
        superAwokenLabel = findViewById(R.id.card_super_awoken_label);
        typeViews[0] = findViewById(R.id.card_type1);
        typeViews[1] = findViewById(R.id.card_type2);
        typeViews[2] = findViewById(R.id.card_type3);
        latentViews[0] = findViewById(R.id.card_latent1);
        latentViews[1] = findViewById(R.id.card_latent2);
        latentViews[2] = findViewById(R.id.card_latent3);
        latentViews[3] = findViewById(R.id.card_latent4);
        latentViews[4] = findViewById(R.id.card_latent5);
        latentViews[5] = findViewById(R.id.card_latent6);
        activeName = findViewById(R.id.card_active_name);
        activeDesc = findViewById(R.id.card_active_description);
        leaderName = findViewById(R.id.card_leader_name);
        leaderDesc = findViewById(R.id.card_leader_description);
        levelView = findViewById(R.id.card_level);
        skillMaxLevel = findViewById(R.id.card_skill_level_max);
        skillTurns = findViewById(R.id.card_skill_turns);
        skillLabel = findViewById(R.id.card_skill_label); //Needed to hide when active skill is None
        skillMaxLabel = findViewById(R.id.card_skill_max_label);
        skillTurnsLabel = findViewById(R.id.card_skill_turns_label);
        assistActiveName = findViewById(R.id.card_active_name_inherit);
        assistActiveDesc = findViewById(R.id.card_active_description_inherit);
        assistSkillMaxLevel = findViewById(R.id.card_skill_level_max_inherit);
        assistSkillTurns = findViewById(R.id.card_skill_turns_inherit);
        assistSkillLabel = findViewById(R.id.card_skill_label_inherit); //Needed to hide when active skill is None
        assistSkillMaxLabel = findViewById(R.id.card_skill_max_label_inherit);
        assistSkillTurnsLabel = findViewById(R.id.card_skill_turns_label_inherit);
        assistIcon = findViewById(R.id.card_inherit_thumbnail);
        assistIs297 = findViewById(R.id.card_inherit_powerup);
        assistLevelView = findViewById(R.id.card_inherit_level);

        levelView.addTextChangedListener(new NumberTextWatcher(0));
        hpPlusView.addTextChangedListener(new NumberTextWatcher(1));
        atkPlusView.addTextChangedListener(new NumberTextWatcher(2));
        rcvPlusView.addTextChangedListener(new NumberTextWatcher(3));
        skillLevelView.addTextChangedListener(new NumberTextWatcher(4));
        assistLevelView.addTextChangedListener(new NumberTextWatcher(5));
        assistSkillLevelView.addTextChangedListener(new NumberTextWatcher(6));

        teamid = getIntent().getIntExtra(TeamEditorActivity.MSG_TEAMNUM, 0);
        teammateid = getIntent().getIntExtra(TeamEditorActivity.MSG_TEAMMATE, 0);

        gameState = GameState.GameStateSingleton.getGameState(this.getApplication());
        fillLayout();
        fillAssistLayout();

    }

    //Called to update the card layout after picking a monster
    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent)
    {
        if (responseCode > -1)
        {
            monsterid = responseCode;
            if (requestCode == 1)
            {
                card.setMonster(gameState.getMonsters().get(responseCode));
                if (card.getLevel() > card.getMonster().getMaxLevel())
                    card.setLevel(card.getMonster().getMaxLevel());
                fillLayout();
            } else if (requestCode == 2)
            {
                card.getInheritCard().setMonster(gameState.getMonsters().get(responseCode));
                if (card.getInheritCard().getLevel() > card.getInheritCard().getMonster().getMaxLevel())
                    card.getInheritCard().setLevel(card.getInheritCard().getMonster().getMaxLevel());
                fillAssistLayout();
                card.computeStats();
                updateStats();
            }
        }
    }

    @Override
    public void onBackPressed()
    {
        setResult(teammateid);
        gameState.saveTeamPrivate(teamid);
        super.onBackPressed();
    }

    //Called to update the raw stats whenever a change may cause them to change.
    private void updateStats()
    {
        hpView.setText(String.format(Locale.getDefault(), "HP:%d", card.getHealth()));
        atkView.setText(String.format(Locale.getDefault(), "ATK:%d", card.getAttack()));
        rcvView.setText(String.format(Locale.getDefault(), "RCV:%d", card.getRecovery()));
    }

    private void updateLatents()
    {
        int awokenID;
        BitmapDrawable resource;
        for (int i = 0; i < 6; i++)
        {
            awokenID = card.getLatent(i);
            if (awokenID != LatentAwoken.LATENT_PLACEHOLDER)
            {
                resource = (BitmapDrawable) ResourcesCompat.getDrawable(getResources(), LatentAwoken.resourceids[awokenID], null);
                latentViews[i].setImageBitmap(resource.getBitmap());
                latentViews[i].setVisibility(View.VISIBLE);
            } else
            {
                latentViews[i].setVisibility(View.GONE);
            }
        }
    }

    private void updateSkill()
    {
        skillTurns.setText((String.format(Locale.getDefault(), "%d", gameState.getSkill(card.getMonster().getActiveID()).getTurnsAt(card.getSkillLevels()))));
        skillMaxLevel.setText((String.format(Locale.getDefault(), "%d", gameState.getSkill(card.getMonster().getActiveID()).getLevels())));
        Card inherit = card.getInheritCard();
        if (inherit.getID() != 0)
        {
            int maxLevel = gameState.getSkill(inherit.getMonster().getActiveID()).getLevels();
            int turns = gameState.getSkill(card.getMonster().getActiveID()).getTurnsAt(card.getSkillLevels()) + gameState.getSkill(inherit.getMonster().getActiveID()).getTurnsAt(inherit.getSkillLevels());
            assistSkillTurns.setText((String.format(Locale.getDefault(), "%d", turns)));
            assistSkillMaxLevel.setText((String.format(Locale.getDefault(), "%d", maxLevel)));
        }
    }

    private void fillAssistLayout()
    {
        int awokenID;
        BitmapDrawable resource;
        assistid = card.getInheritCard().getID();
        new Thread(new Runnable()
        {
            public void run()
            {
                //Use a thread to load bitmaps since it might take a little more time than usual
                final Bitmap bitmap = loadThumbnail(assistid);
                assistIcon.post(new Runnable()
                {
                    public void run()
                    {
                        if (bitmap != null)
                        {
                            assistIcon.setImageBitmap(bitmap);
                        }
                    }
                });
            }
        }).start();
        if (card.getInheritCard().getID() != 0)
        {
            Card inherit = card.getInheritCard();
            assistSkillLevelView.setVisibility(View.VISIBLE);
            assistSkillLabel.setVisibility(View.VISIBLE);
            assistSkillMaxLevel.setVisibility(View.VISIBLE);
            assistSkillMaxLabel.setVisibility(View.VISIBLE);
            assistSkillTurns.setVisibility(View.VISIBLE);
            assistSkillTurnsLabel.setVisibility(View.VISIBLE);
            assistSkillLevelView.setText(String.format(Locale.getDefault(), "%d", inherit.getSkillLevels()));
            Skill activeSkill = gameState.getSkill(inherit.getMonster().getActiveID());
            assistActiveName.setVisibility(View.VISIBLE); assistActiveName.setText(activeSkill.getName());
            assistActiveDesc.setVisibility(View.VISIBLE); assistActiveDesc.setText(activeSkill.getDescription());
            int[] awokenSkills = inherit.getMonster().getAwokenSkills();
            if (awokenSkills[0] == AwokenSkill.AWOKEN_AWOKENASSIST)
            {
                for (int i = 0; i < 9; i++)
                {
                    awokenID = awokenSkills[i];
                    if (awokenID != 0)
                    {
                        //Awoken level is always max for assists, since you must inherit at max level
                        //You could get a incomplete awoken set if a card is buffed, but not worth simulating
                        resource = (BitmapDrawable) ResourcesCompat.getDrawable(getResources(), AwokenSkill.resourceids[awokenID], null);
                        assistAwokenViews[i].setImageBitmap(resource.getBitmap());
                        assistAwokenViews[i].setVisibility(View.VISIBLE);
                    } else
                    {
                        assistAwokenViews[i].setVisibility(View.INVISIBLE);
                    }
                }
            }
            else
            {
                for (int i = 0; i < 9; i++)
                {
                    assistAwokenViews[i].setVisibility(View.GONE);
                }
            }
            assistIs297.setChecked(inherit.getPowerups() == 297);
            assistLevelView.setText(Integer.toString(inherit.getLevel()));
        }
        else
        {
            for (int i = 0; i < 9; i++)
            {
                assistAwokenViews[i].setVisibility(View.GONE);
            }
            assistSkillLevelView.setVisibility(View.GONE);
            assistSkillLabel.setVisibility(View.GONE);
            assistSkillMaxLevel.setVisibility(View.GONE);
            assistSkillMaxLabel.setVisibility(View.GONE);
            assistSkillTurns.setVisibility(View.GONE);
            assistSkillTurnsLabel.setVisibility(View.GONE);
            assistActiveName.setVisibility(View.GONE);
            assistActiveDesc.setVisibility(View.GONE);
        }
    }

    private void fillLayout()
    {
        card = gameState.getTeam(teamid)[teammateid];
        Monster monster = card.getMonster();
        Skill leaderSkill = gameState.getSkill(monster.getLeaderID());
        Skill activeSkill = gameState.getSkill(monster.getActiveID());
        activeName.setText(activeSkill.getName());
        activeDesc.setText(activeSkill.getDescription());
        leaderName.setText(leaderSkill.getName());
        leaderDesc.setText(leaderSkill.getDescription());
        monsterid = monster.getId();
        new Thread(new Runnable()
        {
            public void run()
            {
                //Use a thread to load bitmaps since it might take a little more time than usual
                final Bitmap bitmap = loadThumbnail(monsterid);
                icon.post(new Runnable()
                {
                    public void run()
                    {
                        if (bitmap != null)
                        {
                            icon.setImageBitmap(bitmap);
                        }
                    }
                });
            }
        }).start();
        updateStats();
        hpPlusView.setText(String.format(Locale.getDefault(), "%d", card.getHpPowerups()));
        atkPlusView.setText(String.format(Locale.getDefault(), "%d", card.getAtkPowerups()));
        rcvPlusView.setText(String.format(Locale.getDefault(), "%d", card.getRcvPowerups()));
        levelView.setText(String.format(Locale.getDefault(), "%d", card.getLevel()));
        if (monster.getActiveID() != 0)
        {
            //Don't set number in updateSkill, since this would cause an infinite loop in triggering the TextListener.
            skillLevelView.setVisibility(View.VISIBLE);
            skillLabel.setVisibility(View.VISIBLE);
            skillMaxLevel.setVisibility(View.VISIBLE);
            skillMaxLabel.setVisibility(View.VISIBLE);
            skillTurns.setVisibility(View.VISIBLE);
            skillTurnsLabel.setVisibility(View.VISIBLE);
            skillLevelView.setText(String.format(Locale.getDefault(), "%d", card.getSkillLevels()));
            updateSkill();
        }
        else
        {
            skillLevelView.setVisibility(View.GONE);
            skillLabel.setVisibility(View.GONE);
            skillMaxLevel.setVisibility(View.GONE);
            skillMaxLabel.setVisibility(View.GONE);
            skillTurns.setVisibility(View.GONE);
            skillTurnsLabel.setVisibility(View.GONE);
        }


        BitmapDrawable resource;
        int typeid;
        for (int i = 0; i < 3; i++)
        {
            typeid = monster.getType(i);
            if (typeid != -1)
            {
                resource = (BitmapDrawable) ResourcesCompat.getDrawable(getResources(), MonsterType.resourceids[typeid], null);
                typeViews[i].setVisibility(View.VISIBLE);
                typeViews[i].setImageBitmap(resource.getBitmap());
            } else
                typeViews[i].setVisibility(View.GONE);
        }
        int awokenID;
        for (int i = 0; i < 9; i++)
        {
            awokenID = monster.getAwokenSkills()[i];
            if (awokenID != 0)
            {
                resource = (BitmapDrawable) ResourcesCompat.getDrawable(getResources(), AwokenSkill.resourceids[awokenID], null);
                awokenViews[i + 1].setImageBitmap(resource.getBitmap());
                awokenViews[i + 1].setVisibility(View.VISIBLE);
                if (i < card.getAwokenLevel())
                    awokenViews[i + 1].setAlpha(1.0f);
                else
                    awokenViews[i + 1].setAlpha(0.3f);
            } else
            {
                awokenViews[i + 1].setVisibility(View.INVISIBLE);
            }
        }
        updateLatents();
        int numSuperAwokens = monster.getNumSuperAwokens();
        if (numSuperAwokens > 0)
        {
            superAwokenLabel.setVisibility(View.VISIBLE);
            int superAwoken = card.getSuperAwoken();
            for (int i = 0; i < 9; i++)
            {
                awokenID = monster.getSuperAwokenSkills()[i];
                if (i < numSuperAwokens)
                {
                    resource = (BitmapDrawable) ResourcesCompat.getDrawable(getResources(), AwokenSkill.resourceids[awokenID], null);
                }
                else
                {
                    resource = (BitmapDrawable) ResourcesCompat.getDrawable(getResources(), R.drawable.latent_none, null);
                }
                superAwokenViews[i].setVisibility(View.VISIBLE);
                superAwokenViews[i].setImageBitmap(resource.getBitmap());
                if (superAwoken == i)
                    superAwokenViews[i].setAlpha(1.0f);
                else
                    superAwokenViews[i].setAlpha(0.3f);
            }
        } else
        {
            superAwokenLabel.setVisibility(View.GONE);
            for (int i = 0; i < 9; i++)
            {
                superAwokenViews[i].setVisibility(View.GONE);
            }
        }
    }

    private Bitmap loadThumbnail(int id)
    {
        InputStream bitmapStream = null;
        try
        {
            bitmapStream = getResources().getAssets().open(String.format(Locale.getDefault(), "icon/%04d.png", id));
        } catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
        BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bitmapStream);
        return bitmapDrawable.getBitmap();
    }

    public void onAwokenClick(View sender)
    {
        //For convenience, the buttons in the UI define a tag value to make figuring out what button was clicked easier
        int newLevel = Integer.parseInt((String)sender.getTag());
        //Card card = gameState.getTeam(teamid)[teammateid];

        card.setAwokenLevel(newLevel);
        //Update the alpha on the ImageViews
        for (int i = 0; i < 10; i++)
        {
            if (i <= card.getAwokenLevel())
                awokenViews[i].setAlpha(1.0f);
            else
                awokenViews[i].setAlpha(0.3f);
        }
        updateStats();
    }

    public void onSuperAwokenClick(View sender)
    {
        //Card card = gameState.getTeam(teamid)[teammateid];
        int oldAwoken = card.getSuperAwoken();
        int newAwoken = Integer.parseInt((String)sender.getTag());

        if (newAwoken < card.getMonster().getNumSuperAwokens()) //Don't let setting gray SA
        {
            if (oldAwoken == newAwoken)
            {
                newAwoken = -1;
            }
            card.setSuperAwoken(newAwoken);

            if (oldAwoken != -1)
                superAwokenViews[oldAwoken].setAlpha(0.3f);
            if (newAwoken != -1)
                superAwokenViews[newAwoken].setAlpha(1.0f);
            updateStats(); //do any monsters have stat boost LBs? Just in case...
        }
    }

    public void onLatentSlotClick(View sender)
    {
        if (monsterid != 0)
        {
            int slot = Integer.parseInt((String) sender.getTag());

            //Only show a pruned list if you've clicked the final slot, since you can't fit a double latent there
            showLatentPickerDialog(slot, slot == 5);
        }
    }

    public void updateLatent(int slot, int id)
    {
        card.setLatent(slot, id);
        updateLatents();
        updateStats();
    }

    public void onIconClick(View sender)
    {
        Intent intent = new Intent(this, MonsterList.class);
        startActivityForResult(intent, 1);
    }

    public void onAssistIconClick(View sender)
    {
        if (card.getMonster().getActiveID() != 0)
        {
            Intent intent = new Intent(this, MonsterList.class);
            intent.putExtra(PARAM_LISTMODE, true);
            startActivityForResult(intent, 2);
        }
    }

    public void onAssist297Click(View sender)
    {
        CheckBox box = (CheckBox)sender;
        if (box.isChecked())
        {
            card.getInheritCard().setHpPowerups(99);
            card.getInheritCard().setAtkPowerups(99);
            card.getInheritCard().setRcvPowerups(99);
        }
        else
        {
            card.getInheritCard().setHpPowerups(0);
            card.getInheritCard().setAtkPowerups(0);
            card.getInheritCard().setRcvPowerups(0);
        }
        card.computeStats();
        updateStats();
    }

    private class NumberTextWatcher implements TextWatcher
    {
        public int mode;
        public NumberTextWatcher(int mode)
        {
            this.mode = mode;
        }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {
        }

        @Override
        public void afterTextChanged(Editable s)
        {
            //For convenience, let you empty the text with no ill effects
            if (s.toString().compareTo("") != 0)
            {
                if (mode == 0 || mode == 5)
                {
                    TextView view;
                    Card lcard;
                    if (mode == 0)
                    {
                        view = levelView;
                        lcard = card;
                    }
                    else
                    {
                        view = assistLevelView;
                        lcard = card.getInheritCard();
                    }
                    try
                    {
                        int newLevel = Integer.parseInt(s.toString());
                        if (newLevel > lcard.getMonster().getLBMaxLevel())
                        {
                            lcard.setLevel(lcard.getMonster().getLBMaxLevel());
                            view.setText(Integer.toString(lcard.getMonster().getLBMaxLevel()));
                        } else if (newLevel < 1)
                        {
                            lcard.setLevel(1);
                            view.setText("1");
                        } else
                        {
                            lcard.setLevel(newLevel);
                        }
                    } catch (Exception e)
                    {
                        lcard.setLevel(lcard.getMonster().getMaxLevel());
                        view.setText(Integer.toString(lcard.getMonster().getMaxLevel()));
                    }
                    card.computeStats();
                    updateStats();

                }
                else if (mode < 4)
                {
                    int newLevel = 0;
                    try
                    {
                        newLevel = Integer.parseInt(s.toString());
                        if (newLevel > 99)
                        {
                            newLevel = 99;
                            switch(mode)
                            {
                                case 1:
                                    hpPlusView.setText("99");
                                    break;
                                case 2:
                                    atkPlusView.setText("99");
                                    break;
                                case 3:
                                    rcvPlusView.setText("99");
                                    break;
                            }
                        } else if (newLevel < 0)
                        {
                            newLevel = 0;
                            switch(mode)
                            {
                                case 1:
                                    hpPlusView.setText("0");
                                    break;
                                case 2:
                                    atkPlusView.setText("0");
                                    break;
                                case 3:
                                    rcvPlusView.setText("0");
                                    break;
                            }
                        }
                    } catch (Exception e)
                    {
                        card.setLevel(card.getMonster().getMaxLevel());
                    }
                    switch(mode)
                    {
                        case 1:
                            card.setHpPowerups(newLevel);
                            break;
                        case 2:
                            card.setAtkPowerups(newLevel);
                            break;
                        case 3:
                            card.setRcvPowerups(newLevel);
                            break;
                    }
                    updateStats();
                }
                else if (mode == 4 || mode == 6)
                {
                    Card lcard;
                    Skill skill;// = gameState.getSkill(card.getMonster().getActiveID());
                    EditText lview;
                    if (mode == 4)
                    {
                        lcard = card;
                        skill = gameState.getSkill(card.getMonster().getActiveID());
                        lview = skillLevelView;
                    }
                    else
                    {
                        lcard = card.getInheritCard();
                        skill = gameState.getSkill(card.getInheritCard().getMonster().getActiveID());
                        lview = assistSkillLevelView;
                    }
                    try
                    {
                        int newLevel = Integer.parseInt(s.toString());
                        if (newLevel > skill.getLevels())
                        {
                            lcard.setSkillLevels(skill.getLevels());
                            lview.setText(skill.getLevels());
                        } else if (newLevel < 1)
                        {
                            lcard.setSkillLevels(1);
                            lview.setText("1");
                        } else
                        {
                            lcard.setSkillLevels(newLevel);
                        }
                    } catch (Exception e)
                    {
                        lcard.setSkillLevels(skill.getLevels());
                        lview.setText(Integer.toString(skill.getLevels()));
                    }
                    updateSkill();
                }
            }
        }
    }

    public void showLatentPickerDialog(int slot, boolean shortList)
    {
        FragmentManager fm = getSupportFragmentManager();

        //Generate a fragment transaction for the Latent picker dialog. Shortlist shows only single
        //slot latents.
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag("latentDialog");
        if (prev != null) {
            ft.remove(prev);
        }
        //ara?
        ft.addToBackStack(null);

        DialogFragment dialog = LatentPickerDialogFragment.newInstance(slot, shortList, card.getMonster().getType(0),
                card.getMonster().getType(1), card.getMonster().getType(2));
        dialog.show(ft, "latentDialog");
    }
}
