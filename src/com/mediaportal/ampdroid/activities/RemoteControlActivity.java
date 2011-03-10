package com.mediaportal.ampdroid.activities;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mediaportal.ampdroid.R;
import com.mediaportal.ampdroid.api.DataHandler;
import com.mediaportal.ampdroid.api.IClientControlListener;
import com.mediaportal.ampdroid.api.PowerModes;
import com.mediaportal.ampdroid.api.RemoteCommands;
import com.mediaportal.ampdroid.data.commands.RemoteKey;
import com.mediaportal.ampdroid.remote.RemoteNowPlaying;
import com.mediaportal.ampdroid.remote.RemotePlugin;
import com.mediaportal.ampdroid.remote.RemoteStatusMessage;
import com.mediaportal.ampdroid.remote.RemoteVolumeMessage;
import com.mediaportal.ampdroid.remote.RemoteWelcomeMessage;
import com.mediaportal.ampdroid.utils.Util;

public class RemoteControlActivity extends BaseActivity implements IClientControlListener {
   protected class SendKeyTask extends AsyncTask<RemoteKey, String, String> {
      private DataHandler mController;
      private boolean mRepeat;
      private Context mContext;

      protected SendKeyTask(Context _parent) {
         mContext = _parent;
      }

      private SendKeyTask(Context _parent, DataHandler _controller) {
         this(_parent);
         mController = _controller;
      }

      private SendKeyTask(Context _parent, DataHandler _controller, boolean _repeat) {
         this(_parent, _controller);
         mController = _controller;
         mRepeat = _repeat;
      }

      @Override
      protected String doInBackground(RemoteKey... _keys) {
         if (mController.isClientControlConnected()) {
            mController.sendRemoteButton((RemoteKey) _keys[0]);

            waitForMilliseconds(500);

            while (mRepeat) {
               try {
                  mController.sendRemoteButtonDown((RemoteKey) _keys[0], 60);
                  Thread.sleep(1000);
               } catch (InterruptedException e) {
                  e.printStackTrace();
               }
            }

            /*
             * if (mRepeat) {
             * 
             * 
             * mController.sendRemoteButtonDown((RemoteKey) _keys[0], 40);
             * waitForMilliseconds(120);
             * 
             * while (mRepeat) { mController.sendRemoteButtonDown((RemoteKey)
             * _keys[0], 20); waitForMilliseconds(1000); } }
             */
            return null;
         } else {
            return "Remote not connected";
         }
      }

      private void waitForMilliseconds(int _ms) {
         int msDone = 0;
         while (mRepeat && msDone < _ms) {
            try {
               Thread.sleep(10);
            } catch (InterruptedException e) {
               e.printStackTrace();
            }
            msDone += 10;
         }
      }

      @Override
      protected void onPostExecute(String _result) {
         if (_result != null) {
            Util.showToast(mContext, _result);
         }
      }

      public void setRepeat(boolean _repeat) {
         this.mRepeat = _repeat;
      }

      public boolean getRepeat() {
         return mRepeat;
      }
   }

   protected class SendKeyUpTask extends AsyncTask<RemoteKey, String, String> {
      private DataHandler mController;
      private Context mContext;

      protected SendKeyUpTask(Context _parent) {
         mContext = _parent;
      }

      private SendKeyUpTask(Context _parent, DataHandler _controller) {
         this(_parent);
         mController = _controller;
      }

      @Override
      protected String doInBackground(RemoteKey... _keys) {
         mController.sendRemoteButtonUp();
         return null;
      }
   }

   private StatusBarActivityHandler mStatusBarHandler;
   private TextView mStatusLabel;
   private DataHandler mService;

   @Override
   public void onCreate(Bundle _savedInstanceState) {
      super.onCreate(_savedInstanceState);
      setContentView(R.layout.remotecontrolactivity);

      mService = DataHandler.getCurrentRemoteInstance();
      mService.addClientControlListener(this);

      mStatusBarHandler = new StatusBarActivityHandler(this, mService);
      mStatusBarHandler.setupRemoteStatus();

      mStatusLabel = (TextView) findViewById(R.id.TextViewRemoteState);

      final ImageButton backButton = (ImageButton) findViewById(R.id.ImageButtonBack);
      backButton.setOnTouchListener(new OnTouchListener() {
         @Override
         public boolean onTouch(View _view, MotionEvent _event) {
            if (_event.getAction() == MotionEvent.ACTION_DOWN) {
               Util.Vibrate(_view.getContext(), 30);
               new SendKeyTask(_view.getContext(), mService).execute(RemoteCommands.backButton);
               backButton.setImageResource(R.drawable.remote_back_sel);
               return true;
            }
            if (_event.getAction() == MotionEvent.ACTION_UP) {
               backButton.setImageResource(R.drawable.remote_back);
               return true;
            }
            return false;
         }
      });

      final ImageButton infoButton = (ImageButton) findViewById(R.id.ImageButtonInfo);
      infoButton.setOnTouchListener(new OnTouchListener() {
         @Override
         public boolean onTouch(View _view, MotionEvent _event) {
            if (_event.getAction() == MotionEvent.ACTION_DOWN) {
               Util.Vibrate(_view.getContext(), 30);
               new SendKeyTask(_view.getContext(), mService).execute(RemoteCommands.infoButton);
               infoButton.setImageResource(R.drawable.remote_info_sel);
               return true;
            }
            if (_event.getAction() == MotionEvent.ACTION_UP) {
               infoButton.setImageResource(R.drawable.remote_info);
               return true;
            }
            return false;
         }
      });

      final ImageButton homeButton = (ImageButton) findViewById(R.id.ImageButtonHome);
      homeButton.setOnTouchListener(new OnTouchListener() {
         @Override
         public boolean onTouch(View _view, MotionEvent _event) {
            if (_event.getAction() == MotionEvent.ACTION_DOWN) {
               Util.Vibrate(_view.getContext(), 30);

               new SendKeyTask(_view.getContext(), mService).execute(RemoteCommands.homeButton);
               homeButton.setImageResource(R.drawable.remote_home_sel);
               return true;
            }
            if (_event.getAction() == MotionEvent.ACTION_UP) {
               homeButton.setImageResource(R.drawable.remote_home);
               return true;
            }
            return false;
         }
      });

      final ImageButton remote = (ImageButton) findViewById(R.id.ImageButtonArrows);

      remote.setOnTouchListener(new OnTouchListener() {
         SendKeyTask task = null;

         @Override
         public boolean onTouch(View _view, MotionEvent _event) {
            if (_event.getAction() == MotionEvent.ACTION_DOWN) {
               Util.Vibrate(_view.getContext(), 30);
               float x = _event.getX();
               float y = _event.getY();
               int index = getTouchPart(x, y);

               switch (index) {
               case 0:
                  remote.setImageResource(R.drawable.remote_left);
                  if (task != null) {
                     task.setRepeat(false);
                  }
                  task = (SendKeyTask) new SendKeyTask(_view.getContext(), mService)
                        .execute(RemoteCommands.leftButton);
                  break;
               case 1:
                  remote.setImageResource(R.drawable.remote_right);
                  if (task != null) {
                     task.setRepeat(false);
                  }
                  task = (SendKeyTask) new SendKeyTask(_view.getContext(), mService)
                        .execute(RemoteCommands.rightButton);
                  break;
               case 2:
                  remote.setImageResource(R.drawable.remote_up);
                  // mService.sendRemoteButtonDown(RemoteCommands.upButton,
                  // 100);
                  if (task != null) {
                     task.setRepeat(false);
                  }
                  task = (SendKeyTask) new SendKeyTask(_view.getContext(), mService, true)
                        .execute(RemoteCommands.upButton);
                  break;
               case 3:
                  remote.setImageResource(R.drawable.remote_down);
                  // mService.sendRemoteButtonDown(RemoteCommands.downButton,
                  // 100);
                  if (task != null) {
                     task.setRepeat(false);
                  }
                  task = (SendKeyTask) new SendKeyTask(_view.getContext(), mService, true)
                        .execute(RemoteCommands.downButton);
                  break;
               case 4:
                  remote.setImageResource(R.drawable.remote_enter);
                  new SendKeyTask(_view.getContext(), mService).execute(RemoteCommands.okButton);
                  break;
               }

            }
            if (_event.getAction() == MotionEvent.ACTION_UP) {
               if (task != null) {
                  task.setRepeat(false);
               }
               new SendKeyUpTask(_view.getContext(), mService).execute();
               // mService.sendRemoteButtonUp();
               remote.setImageResource(R.drawable.remote_default);
            }
            return false;
         }

         private int getTouchPart(float _x, float _y) {
            int width = remote.getWidth();
            int height = remote.getHeight();

            if (_x < width / 3 && _y > height / 6 && _y < height - height / 6) {
               return 0;
            }

            if (_x > width * 0.66 && _y > height / 6 && _y < height - height / 6) {
               return 1;
            }

            if (_y < height / 3 && _x > width / 6 && _x < width - width / 6) {
               return 2;
            }

            if (_y > height * 0.66 && _x > width / 6 && _x < width - width / 6) {
               return 3;
            }

            if (_x > width / 3 && _x < width * 0.66 && _y > height / 3 && _y < height * 0.66) {
               return 4;// middle
            }
            return -99;
         }

      });

   }

   @SuppressWarnings("unchecked")
   @Override
   public void messageReceived(Object _message) {

      if(_message != null){
         if(_message.getClass().equals(RemoteStatusMessage.class)){
            String module = ((RemoteStatusMessage) _message).getCurrentModule();
            mStatusLabel.setText(module);
            mStatusBarHandler.setStatusText("Change module to " + module);
            mStatusBarHandler.setStatus((RemoteStatusMessage)_message);
         }
         else if(_message.getClass().equals(RemoteNowPlaying.class)){
            //String module = (String) userData.get("CurrentModule");
            //mStatusLabel.setText(module);
            mStatusBarHandler.setNowPlaying((RemoteNowPlaying) _message);
         }
         else if(_message.getClass().equals(RemotePlugin[].class)){
            
         }
         else if(_message.getClass().equals(RemoteWelcomeMessage.class)){
            RemoteVolumeMessage vol = ((RemoteWelcomeMessage) _message).getVolume();
            mStatusBarHandler.setVolume(vol);
         }
         else if(_message.getClass().equals(RemoteVolumeMessage.class)){
            RemoteVolumeMessage vol = ((RemoteVolumeMessage) _message);
            mStatusBarHandler.setVolume(vol);
         }
      }

      
   }

   @Override
   public void stateChanged(String _state) {
      mStatusBarHandler.setStatusText(_state);
   }

   @Override
   public boolean onCreateOptionsMenu(Menu _menu) {
      SubMenu viewItem = _menu.addSubMenu(0, Menu.FIRST + 1, Menu.NONE, getString(R.string.remote_menu_morecommands));
      createMoreCommandsMen8(viewItem);

      SubMenu powerItem = _menu.addSubMenu(0, Menu.FIRST + 2, Menu.NONE, getString(R.string.remote_menu_powermodes));
      createPowerModeMenu(powerItem);
      
      SubMenu pluginsItem = _menu.addSubMenu(0, Menu.FIRST + 3, Menu.NONE, getString(R.string.remote_menu_plugins));
      createPluginsMenu(pluginsItem);

      return true;
   }

   private void createPluginsMenu(SubMenu _menu) {
      MenuItem stopSettingsItem = _menu.add(0, Menu.FIRST, Menu.NONE, "List of all plugins");
      stopSettingsItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {
         @Override
         public boolean onMenuItemClick(MenuItem item) {
            mService.requestPlugins();
            return false;
         }
      });
      
   }

   private void createPowerModeMenu(SubMenu _powerItem) {
      MenuItem logoffSettingsItem = _powerItem.add(0, Menu.FIRST, Menu.NONE, getString(R.string.remote_logoff));
      logoffSettingsItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {
         @Override
         public boolean onMenuItemClick(MenuItem item) {
            mService.sendSetPowerModeCommand(PowerModes.Logoff);
            return true;
         }
      });
      
      MenuItem suspendSettingsItem = _powerItem.add(0, Menu.FIRST, Menu.NONE, getString(R.string.remote_suspend));
      suspendSettingsItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {
         @Override
         public boolean onMenuItemClick(MenuItem item) {
            mService.sendSetPowerModeCommand(PowerModes.Suspend);
            return true;
         }
      });
      
      MenuItem hibernateSettingsItem = _powerItem.add(0, Menu.FIRST, Menu.NONE, getString(R.string.remote_hibernate));
      hibernateSettingsItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {
         @Override
         public boolean onMenuItemClick(MenuItem item) {
            mService.sendSetPowerModeCommand(PowerModes.Hibernate);
            return true;
         }
      });
      
      MenuItem rebootSettingsItem = _powerItem.add(0, Menu.FIRST, Menu.NONE, getString(R.string.remote_reboot));
      rebootSettingsItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {
         @Override
         public boolean onMenuItemClick(MenuItem item) {
            mService.sendSetPowerModeCommand(PowerModes.Reboot);
            return true;
         }
      });
      
      MenuItem shutdownSettingsItem = _powerItem.add(0, Menu.FIRST, Menu.NONE, getString(R.string.remote_shutdown));
      shutdownSettingsItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {
         @Override
         public boolean onMenuItemClick(MenuItem item) {
            mService.sendSetPowerModeCommand(PowerModes.Shutdown);
            return true;
         }
      });
      
      MenuItem exitSettingsItem = _powerItem.add(0, Menu.FIRST, Menu.NONE, getString(R.string.remote_exit));
      exitSettingsItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {
         @Override
         public boolean onMenuItemClick(MenuItem item) {
            mService.sendSetPowerModeCommand(PowerModes.Exit);
            return true;
         }
      });
   }

   private void createMoreCommandsMen8(SubMenu _menu) {
      MenuItem stopSettingsItem = _menu.add(0, Menu.FIRST, Menu.NONE, "Stop");
      stopSettingsItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {
         @Override
         public boolean onMenuItemClick(MenuItem item) {
            mService.sendRemoteButton(RemoteCommands.stopButton);
            return true;
         }
      });

      MenuItem switchFullscreenSettingsItem = _menu.add(0, Menu.FIRST + 1, Menu.NONE,
            "Switch Fullscreen");
      switchFullscreenSettingsItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {
         @Override
         public boolean onMenuItemClick(MenuItem item) {
            mService.sendRemoteButton(RemoteCommands.switchFullscreenButton);
            return true;
         }
      });

      MenuItem subtitlesSettingsItem = _menu.add(0, Menu.FIRST + 2, Menu.NONE, "Subtitles");
      subtitlesSettingsItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {
         @Override
         public boolean onMenuItemClick(MenuItem item) {
            mService.sendRemoteButton(RemoteCommands.subtitlesButton);
            return true;
         }
      });

      MenuItem switchAudioTrackSettingsItem = _menu.add(0, Menu.FIRST + 3, Menu.NONE,
            "Switch Audio Tracks");
      switchAudioTrackSettingsItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {
         @Override
         public boolean onMenuItemClick(MenuItem item) {
            mService.sendRemoteButton(RemoteCommands.audioTrackButton);
            return true;
         }
      });

      MenuItem menuSettingsItem = _menu.add(0, Menu.FIRST + 4, Menu.NONE, "Menu");
      menuSettingsItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {
         @Override
         public boolean onMenuItemClick(MenuItem item) {
            mService.sendRemoteButton(RemoteCommands.menuButton);
            return true;
         }
      });

      MenuItem channelUpSettingsItem = _menu.add(0, Menu.FIRST + 5, Menu.NONE, "Channel Up");
      channelUpSettingsItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {
         @Override
         public boolean onMenuItemClick(MenuItem item) {
            mService.sendRemoteButton(RemoteCommands.channelUpButton);
            return true;
         }
      });

      MenuItem channelDownSettingsItem = _menu.add(0, Menu.FIRST + 6, Menu.NONE, "Channel Down");
      channelDownSettingsItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {
         @Override
         public boolean onMenuItemClick(MenuItem item) {
            mService.sendRemoteButton(RemoteCommands.channelDownButton);
            return true;
         }
      });

      MenuItem recordingSettingsItem = _menu.add(0, Menu.FIRST + 7, Menu.NONE, "Recording");
      recordingSettingsItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {
         @Override
         public boolean onMenuItemClick(MenuItem item) {
            mService.sendRemoteButton(RemoteCommands.recordingButton);
            return true;
         }
      });
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      Window window = getWindow();
      window.setFormat(PixelFormat.RGBA_8888);
   }
}
