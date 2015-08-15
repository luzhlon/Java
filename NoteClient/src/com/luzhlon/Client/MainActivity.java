package com.luzhlon.Client;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.*;
import android.util.DisplayMetrics;
import android.view.*;
import android.widget.*;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import java.io.*;
import java.util.*;

public class MainActivity extends Activity implements AdapterView.OnItemClickListener {
    TextView tvUser;
    TextView tvNoteName;
    TextView tvRemove;
    EditText edContent;
    ListView listNote;
    SimpleAdapter listAdapter;
    private int   lastListIndex = -1; //listNote中上一次的点击位置
    ArrayList<HashMap<String, Object>> listMapArray;
    //listNote 中item被点击时
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        try {
            saveNote();//保存笔记
            TextView item = (TextView)view.findViewById(R.id.textItem);
            loadNote(item.getText().toString());//加载笔记
        } catch (Exception e) {
            Global.ToastMessage(Global.CurAct, e.toString());
        }
        lastListIndex = i;//条目索引
    }
    void onLeaveActivity() {
        //保存笔记
        saveNote();
        //保存离开时的笔记名称
        SharedPreferences.Editor edit = Global.sp.edit();
        edit.putString(Global.LAST_SELECT, tvNoteName.getText().toString());
        edit.commit();
    }
    private long exitTime = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK
                && event.getAction()==KeyEvent.ACTION_DOWN) {
            if(System.currentTimeMillis()-exitTime > 2000) {
                Global.ToastMessage(Global.CurAct, "再按一次退出");
                exitTime = System.currentTimeMillis();
            } else {
                try {
                    onLeaveActivity();
                } catch (Exception e) {
                    Global.ToastMessage(Global.CurAct, e.toString());
                }
                finish();
                System.exit(0);
            }
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                Global.ToastMessage(Global.CurAct, "正在推送所有笔记");
                MainActivity.this.pushNote();
                break;
            case 2:
                Global.ToastMessage(Global.CurAct, "正在获取所有笔记");
                MainActivity.this.pullNote();
                break;
        }
        return true;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        Global.ToastMessage(Global.CurAct, "onCreateOptionsMenu");
        menu.add(0,1,0,"Push All");
        menu.add(0, 2, 0, "Pull All");
        return true;
    }
    //Initialize the object
    void Init() {
        Global.MainAct = this;
        InitSiliding();
        tvNoteName = (TextView)findViewById(R.id.textNoteName);
        tvRemove = (TextView)findViewById(R.id.textRemove);
        edContent = (EditText)findViewById(R.id.editContent);
        listNote.setOnItemClickListener(this);
        listMapArray = new ArrayList<HashMap<String, Object>>();
        listAdapter = new SimpleAdapter(this, listMapArray, R.layout.item,
                new String[]{"textItem", "textItemL"},
                new int[]{R.id.textItem, R.id.textItemL});
    }
    class Note {
        String name;
        String time_create;
        String time_modify;
        String content;
        Note() {

        }
        Note(String name, String time_create, String time_modify, String content) {
            this.name =  name;
            this.time_create =  time_create;
            this.time_modify =  time_modify;
            this.content =  content;
        }
    }
    void pushNote(String noteName) {
        Note note = getNote(noteName);
        Global.handler.send(new MyMsgHandler.callBack() {
            @Override
            public void onResults(Object[] result) {
                String s = (String)result[0];
                switch ((String)result[0]) {
                    case "yes":
                        Global.ToastMessage("Push "+noteName+" success");
                        break;
                    case "no":
                        Global.ToastMessage("Push "+noteName+" failure");
                        break;
                }
            }
        }, "push_note", note.name,
                note.time_create, note.time_modify, note.content);
    }
    void pushNote() {
        String[] files = fileList();
        for(String file:files) {
            pushNote(file);
        }
    }
    void pullNote() {
        Global.handler.send(new MyMsgHandler.callBack() {
            @Override
            public void onResults(Object[] result) {
                switch ((String)result[0]) {
                    case "yes":
                        String[] list = new String[result.length-1];
                        for (int i = 1; i < result.length; i++) {
                            list[i-1] = (String)result[i];
                        }
                        int i = 0;
                        Note note = new Note();
                        while(i<list.length) {
                            note.name = list[i++];
                            note.time_create = list[i++];
                            note.time_modify = list[i++];
                            note.content = list[i++];
                            saveNote(note); //保存笔记
                        }
                        UpdateNoteList();//刷新列表
                        break;
                    case "no":
                        Global.ToastMessage(Global.CurAct, "Pull all note failure");
                        break;
                }
            }
        }, "pull_all_note");
    }
    Note getNote(String noteName) {
        Note note = new Note();
        try {
            InputStreamReader ir =
                    new InputStreamReader(openFileInput(noteName));
            BufferedReader br = new BufferedReader(ir);
            note.name = noteName;
            note.time_create = br.readLine();//日期
            note.time_modify = br.readLine();//上次修改时间
            StringBuffer sb = new StringBuffer();
            char[] buf = new char[10240];
            int n = 0;
            while((n=br.read(buf))>0) {
                sb.append(buf, 0, n);
            }
            char lastchar = sb.charAt(sb.length()-1);
            if(lastchar=='\n' || lastchar=='\r') {
                sb.setCharAt(sb.length()-1, '\0');
            }//最后老是莫名其妙的多出一个换行符，换掉它
            note.content = sb.toString(); //笔记内容
        } catch (Exception e) {
            Global.ToastMessage(Global.CurAct, e.toString());
        }
        return note;
    }
    void loadNote(String noteName) { //加载笔记
        try {
            if(noteName==null || noteName.isEmpty()) {
                //笔记名为空时
                tvNoteName.setText("");
                edContent.setText("");
                return;
            }
            Note note = getNote(noteName);
            tvNoteName.setText(note.name);//笔记名称
            edContent.setText(note.content);//笔记内容
        } catch (Exception e) {
            Global.ToastMessage(e.toString());
        }
    }
    void saveNote(Note note) { //保存笔记
        try {
            File file = getFileStreamPath(note.name);
            file.delete();//先删除原有文件
            FileOutputStream fo = openFileOutput(note.name, MODE_PRIVATE);
            PrintWriter pw = new PrintWriter(fo);
            pw.println(note.time_create);//写入创建日期
            pw.println(note.time_modify);//写入修改日期
            pw.println(note.content);//写入笔记内容
            pw.flush();
            pw.close();
        } catch (Exception e) {
            Global.ToastMessage(e.toString());
        }
    }
    void saveNote() { //保存当前的笔记
        Note note = new Note();
        try {
            note.name = tvNoteName.getText().toString();//笔记名称
            if (note.name.isEmpty()) return; //没有当前笔记
            note.content = edContent.getText().toString();//笔记内容
            BufferedReader br = new BufferedReader
                    (new InputStreamReader(openFileInput(note.name)));
            note.time_create = br.readLine();//读出创建日期
            br.close();
            note.time_modify = Global.GetDateTime();
            saveNote(note);
        } catch (Exception e) {
            Global.ToastMessage(e.toString());
        }
    }
    void UpdateNoteList() { //刷新笔记列表
        String[] notes = fileList();
        FileInputStream in;
        String date;
        BufferedReader br;
        HashMap<String, Object> item;
        try {
            listMapArray.clear();//清空原有列表
            for(String str:notes) { //枚举笔记
                in = openFileInput(str);
                br = new BufferedReader(new InputStreamReader(in));
                date = br.readLine();
                br.close();
                item = new HashMap<String, Object>();
                item.put(Global.TEXTITEM, str);
                item.put(Global.TEXTITEML, date);
                listMapArray.add(item);
            }
        } catch (Exception e) {
            Global.ToastMessage(Global.CurAct, e.toString());
        }
        listNote.setAdapter(listAdapter);
    }
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        Init();
        UpdateNoteList();
    }
    @Override
    public void onPause() {
        super.onPause();
        onLeaveActivity();
    }
    @Override
    public void onResume() { //Get focus
        super.onResume();
        Global.CurAct = this;
        //检测用户登录
        if(!Global.user.isEmpty()) {
            tvUser.setTextColor(0xFF00FF00);//Green
            tvUser.setText(Global.user);
            onSignIn();
        } else {
            tvUser.setTextColor(0xFFFF0000);//Red
            tvUser.setText("[ 未登录 ]");
            onLogOut();
        }
        //加载上次离开时的笔记
        String note = Global.sp.getString(Global.LAST_SELECT, "");
        loadNote(note);
    }
    String getTextOfItem(int i) {
        try {
            HashMap<String, Object> v =
                    (HashMap<String, Object>) listNote.getItemAtPosition(i);
            if (v == null) return null;
            return (String)v.get(Global.TEXTITEM);
        } catch (Exception e) {
            Global.ToastMessage(Global.CurAct, e.toString());
            return null;
        }
    }
    String getSelectedText() {
        if(lastListIndex<0) return null;
        return getTextOfItem(lastListIndex);
    }

    public void onSettingClick(View v) {
        //Toast.makeText(this, "onSetting", Toast.LENGTH_SHORT).show();
        Global.NextActivity(this, ConfigActivity.class);
    }

    public void onUserClick(View v) { //点击用户名时
        //Toast.makeText(this, "onUser", Toast.LENGTH_SHORT).show();
        if(Global.user.isEmpty()) { //用户尚未登录
            Global.NextActivity(this, SignInActivity.class);
        }
    }
    public void onSignIn() { //用户登录时
        TextView tvPush = (TextView)findViewById(R.id.textPush);
        TextView tvPullAll = (TextView)findViewById(R.id.textPullAll);
        tvPush.setEnabled(true);
        tvPullAll.setEnabled(true);
    }
    public void onLogOut() { //用户下线时
        TextView tvPush = (TextView)findViewById(R.id.textPush);
        TextView tvPullAll = (TextView)findViewById(R.id.textPullAll);
        tvPush.setEnabled(false);
        tvPullAll.setEnabled(false);
    }
    void deleteNote(String name) {
        try {
            String noteName = name;
            if(noteName==null || noteName.isEmpty()) return;
            File file = getFileStreamPath(name);
            file.delete(); //从文件中删除笔记
            UpdateNoteList();//刷新列表
            loadNote(getTextOfItem(0));//加载第一条笔记
            if(!Global.user.isEmpty()) {//用户在线,则将操作同步到服务器
                Global.handler.send(null, "delete_note", noteName);
            }
        } catch (Exception e) {
            Global.ToastMessage(Global.CurAct, e.toString());
        }
    }
    void deleteNote() {
        String noteName = tvNoteName.getText().toString();
        deleteNote(noteName);
    }
    public void onRemoveClick(View v) { //移除笔记
        deleteNote();
    }
    public void onNewClick(View v) { //添加笔记
        EditText noteName = new EditText(this);
        try {
            AlertDialog dlg = new AlertDialog.Builder(this)
                    .setTitle("笔记名称 ~")
                    .setView(noteName)
                    .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            try {
                                String name = noteName.getText().toString();
                                if (!name.isEmpty()) {
                                    FileOutputStream fo = openFileOutput(name, MODE_PRIVATE);
                                    PrintWriter pw = new PrintWriter(fo);
                                    String time = Global.GetDateTime();
                                    pw.println(time);//将时间写入第一行
                                    pw.println(time);//将上次修改时间写入第二行
                                    pw.flush();
                                    pw.close();
                                    MainActivity.this.UpdateNoteList();//刷新笔记列表
                                    saveNote();//保存当前的笔记
                                    loadNote(name);//加载新建的笔记
                                    dialogInterface.cancel();
                                }
                            } catch (Exception e) {
                                Global.ToastMessage(Global.CurAct, e.toString());
                            }
                        }
                    })
                    .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    }).create();
            dlg.show();
        } catch (Exception e) {
            //e.printStackTrace();
            Global.ToastMessage(this, e.toString());
        }
    }
    public void onPushClick(View v) {
        saveNote();
        pushNote(tvNoteName.getText().toString());
    }
    public void onPushAllClick(View v) {
        Global.ToastMessage(Global.CurAct, "正在推送所有笔记");
        MainActivity.this.pushNote();
    }
    public void onPullAllClick(View v) {
        Global.ToastMessage(Global.CurAct, "正在获取所有笔记");
        MainActivity.this.pullNote();
    }
    void InitSiliding() {
        SlidingMenu menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.LEFT);
        // 设置触摸屏幕的模式
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        //menu.setShadowWidthRes(R.dimen.shadow_width);
        //menu.setShadowDrawable(R.drawable.shadow);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        menu.setBehindWidth(dm.widthPixels * 2 / 3);
        // 设置渐入渐出效果的值
        menu.setFadeDegree(0.35f);
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        //为侧滑菜单设置布局
        menu.setMenu(R.layout.leftmenu);

        tvUser = (TextView)menu.findViewById(R.id.textUser);
        listNote = (ListView)menu.findViewById(R.id.listNote);
    }
    protected void onDestroy() {
        super.onDestroy();
    }
}

