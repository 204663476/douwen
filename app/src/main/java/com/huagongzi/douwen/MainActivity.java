package com.huagongzi.douwen;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.LocaleList;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;
import com.huagongzi.douwen.cardswipelayout.CardConfig;
import com.huagongzi.douwen.cardswipelayout.CardItemTouchHelperCallback;
import com.huagongzi.douwen.cardswipelayout.CardLayoutManager;
import com.huagongzi.douwen.cardswipelayout.CardRecyclerView;
import com.huagongzi.douwen.cardswipelayout.OnSwipeListener;
import com.huagongzi.douwen.untils.JustifyTextView;
import com.huagongzi.douwen.untils.LocalAdapter;
import com.huagongzi.douwen.untils.Mydata;
import com.huagongzi.douwen.untils.VerticalScrollview;
import com.huagongzi.douwen.untils.random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.MalformedInputException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.huagongzi.douwen.untils.gethtml.getHtml;


public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, OnSwipeListener<Mydata>, NavigationView.OnNavigationItemSelectedListener {


    //声明一个long类型变量：用于存放上一点击“返回键”的时刻
    private long mExitTime;

    static Toolbar mToolbar;
    static TextView mTitle;

    public static CardRecyclerView recyclerView;
    private List<Mydata> list = new ArrayList<>();

    public static CardItemTouchHelperCallback<Mydata> cardCallback;


    //与收藏有关的控件
    private List<Mydata> locallist = new ArrayList<>();
    private ListView mListView;
    LocalAdapter localAdapter;


    //与评论有关的控件
    private LinearLayout WebLayout,MainLayout;
    private WebView mWebView;
    private Button mWebLayoutClose;
    private ProgressDialog dialog;


    //判断是否刚打开软件  是adapter更新的依据
    static boolean loadRecyclerView = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData(20);
    }


    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setTitle("");
        setSupportActionBar(mToolbar);
        mTitle = (TextView)findViewById(R.id.main_little_tittle);

        //mToolbar.setTitleTextColor(getResources().getColor(R.color.white));
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);




        //喜欢和无感按钮
        ImageButton likeButton = (ImageButton) findViewById(R.id.main_button_like);
        ImageButton hateButton = (ImageButton) findViewById(R.id.main_button_hate);
        likeButton.setOnClickListener(this);
        hateButton.setOnClickListener(this);

        //与收藏有关的
        localAdapter= new LocalAdapter(MainActivity.this,R.layout.list_item,locallist);
        //获取listView
        mListView = (ListView)findViewById(R.id.main_like_list);
        //给listView绑定它的适配器，用setAdapter方法
        mListView.setAdapter(localAdapter);
        // 设置ListView的点击响应事件
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Mydata mydata = locallist.get(position);
                WebLayout.setVisibility(View.VISIBLE);
                MainLayout.setVisibility(View.GONE);
                mWebView.setVisibility(View.GONE);
                mWebView.loadUrl("http://meiriyikan.cn/douwen/dwapi.php?type="
                        + locallist.get(position).getType() + "&id=" + locallist.get(position)
                        .getId() + "&style=web");
                dialog = ProgressDialog.show(MainActivity.this, "Please Wait sometime", null);
            }
        });
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           final int arg2, long arg3) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("确认删除吗");
                builder.setTitle("提示");
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // TODO Auto-generated method stub
                        arg0.dismiss();
                    }
                });
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // TODO Auto-generated method stub
                        //Toast.makeText(MainActivity.this,arg2+"",Toast.LENGTH_SHORT).show();
                        SaveList(locallist.get(arg2),true);
                        locallist.remove(arg2);
                        localAdapter.notifyDataSetChanged();
                        if (locallist.size() == 0){
                            mTitle.setText("抖文");
                            mListView.setVisibility(View.GONE);
                        }
                        arg0.dismiss();
                    }
                });
                builder.create().show();
				return true;     // 这里一定要改为true，代表长按自己消费掉了，若为false，触发长按事件的同时，还会触发点击事件</span></strong>
            }
        });




        WebLayout = (LinearLayout) findViewById(R.id.main_web_layout);
        MainLayout = (LinearLayout)findViewById(R.id.main_main_layout);
        mWebLayoutClose = (Button) findViewById(R.id.main_button_webclose);
        mWebLayoutClose.setOnClickListener(this);
        mWebView = (WebView) findViewById(R.id.main_web_webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setSupportZoom(false);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setGeolocationEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setDatabaseEnabled(true);
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.requestFocus();
        mWebView.getSettings().setTextSize(WebSettings.TextSize.NORMAL);
        mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                dialog.dismiss();
                mWebView.setVisibility(View.VISIBLE);
            }
        });



        recyclerView = (CardRecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(new MyAdapter());

        cardCallback = new CardItemTouchHelperCallback<Mydata>(recyclerView, recyclerView.getAdapter(), list);
        cardCallback.setOnSwipedListener(this);
        final ItemTouchHelper touchHelper = new ItemTouchHelper(cardCallback);
        final CardLayoutManager cardLayoutManager = new CardLayoutManager(recyclerView, touchHelper);
        recyclerView.setLayoutManager(cardLayoutManager);
        recyclerView.addOnItemTouchListener(new OnItemClickListener(recyclerView) {
            @Override
            public void onItemLongClick(RecyclerView.ViewHolder viewHolder, int position) {

            }

            @Override
            public void onItemClick(RecyclerView.ViewHolder viewHolder, int position) {
                //Toast.makeText(MainActivity.this, "position = " + position, Toast.LENGTH_SHORT).show();
                WebLayout.setVisibility(View.VISIBLE);
                MainLayout.setVisibility(View.GONE);
                mWebView.setVisibility(View.GONE);
                mWebView.loadUrl("http://meiriyikan.cn/douwen/dwapi.php?type="
                        + list.get(position).getType() + "&id=" + list.get(position)
                        .getId() + "&style=web");
                dialog = ProgressDialog.show(MainActivity.this, "Please Wait sometime", null);


            }
        });
        touchHelper.attachToRecyclerView(recyclerView);




        LinearLayout mSmallLayout = (LinearLayout)findViewById(R.id.main_small_view);
        Glide.with(this)
                .load(R.drawable.timg)
                //.placeholder(R.drawable.loading)
                //.error(R.drawable.failed)
                .crossFade(1000)
                .bitmapTransform(new BlurTransformation(this,1,1))  // “23”：设置模糊度(在0.0到25.0之间)，默认”25";"4":图片缩放比例,默认“1”。
                .into(new ViewTarget<View, GlideDrawable>(mSmallLayout) {
                    //括号里为需要加载的控件
                    @Override
                    public void onResourceReady(GlideDrawable resource,
                                                GlideAnimation<? super GlideDrawable> glideAnimation) {
                        this.view.setBackground(resource.getCurrent());
                    }
                });
    }


    private void initData(int size) {
        for (int i = 1; i <= size; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String html = getHtml("http://meiriyikan.cn/douwen/dwapi.php?style=json&type=" + random.getType());
                        //String html = getUrl("http://meiriyikan.cn/dwapi.php?id=19&type="+ random.getType());
                        Message msg = new Message();
                        msg.obj = html;
                        myHandler.sendMessage(msg);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        //Toast.makeText(this,list.size()+"",Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i("ansen", "是否有上一个页面:" + mWebView.canGoBack());

        if (WebLayout.getVisibility() == View.VISIBLE && keyCode == KeyEvent.KEYCODE_BACK) {
            WebLayout.setVisibility(View.GONE);
            MainLayout.setVisibility(View.VISIBLE);

            return true;
        }
        if (mWebView.canGoBack() && keyCode == KeyEvent.KEYCODE_BACK) {//点击返回按钮的时候判断有没有上一页
            mWebView.goBack(); // goBack()表示返回webView的上一页面
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //与上次点击返回键时刻作差
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                //大于2000ms则认为是误操作，使用Toast进行提示
                Snackbar.make(mToolbar, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                //并记录下本次点击“返回键”的时刻，以便下次进行判断
                mExitTime = System.currentTimeMillis();
            } else {
                //小于2000ms则认为是用户确实希望退出程序-调用System.exit()方法进行退出
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_like) {
            if (load() != "") {
                mTitle.setText("喜欢");
                mListView.setVisibility(View.VISIBLE);
                localAdapter.notifyDataSetChanged();
                onClickLike();
            }else {
                Snackbar.make(mToolbar,"没有喜欢的，右滑几个试试吧",Snackbar.LENGTH_SHORT).show();
            }
        } else if (id == R.id.nav_main) {
            mTitle.setText("抖文");
            mListView.setVisibility(View.GONE);
        } else if (id == R.id.nav_yw) {
            Intent intent = new Intent();
            //Intent intent = new Intent(Intent.ACTION_VIEW,uri);
            intent.setAction("android.intent.action.VIEW");
            Uri content_url = Uri.parse("http://yw.meiriyikan.cn/");
            intent.setData(content_url);
            startActivity(intent);
        }if (id == R.id.nav_exit){
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @SuppressLint("HandlerLeak")
    Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(msg.obj.toString());
                String type = jsonObject.optString("type");
                String id = jsonObject.optString("id");
                String text = jsonObject.optString("text");
                Mydata mydata = new Mydata(type, id, text);
                list.add(mydata);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            recyclerView.getAdapter().notifyDataSetChanged();
        }
    };


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_button_like:
                //list.remove(0);
                //myAdapter.notifyDataSetChanged();
                cardCallback.handleCardSwipe(CardConfig.SWIPING_RIGHT, 300L);
                break;
            case R.id.main_button_hate:
                cardCallback.handleCardSwipe(CardConfig.SWIPING_LEFT, 300L);
                break;
            case R.id.main_button_webclose:
                MainLayout.setVisibility(View.VISIBLE);
                WebLayout.setVisibility(View.GONE);

        }
    }


    @Override
    public void onSwiping(RecyclerView.ViewHolder viewHolder, float ratio, int direction) {
        MyAdapter.MyViewHolder myHolder = (MyAdapter.MyViewHolder) viewHolder;
        viewHolder.itemView.setAlpha(1 - Math.abs(ratio) * 0.2f);
        if (direction == CardConfig.SWIPING_LEFT) {
            myHolder.dislikeImageView.setAlpha(Math.abs(ratio));
        } else if (direction == CardConfig.SWIPING_RIGHT) {
            myHolder.likeImageView.setAlpha(Math.abs(ratio));
        } else {
            myHolder.dislikeImageView.setAlpha(0f);
            myHolder.likeImageView.setAlpha(0f);
        }
    }


    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, Mydata o, int direction) {
        MyAdapter.MyViewHolder myHolder = (MyAdapter.MyViewHolder) viewHolder;
        viewHolder.itemView.setAlpha(1f);
        myHolder.dislikeImageView.setAlpha(0f);
        myHolder.likeImageView.setAlpha(0f);
        initData(1);
        if (direction == CardConfig.SWIPED_RIGHT){
            SaveList(o,false);
            Snackbar.make(mToolbar,"添加喜欢",1500).show();
        }
        //Toast.makeText(MainActivity.this, direction == CardConfig.SWIPED_LEFT ? "swiped left" : "swiped right", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSwipedClear() {
        Toast.makeText(MainActivity.this, "data clear", Toast.LENGTH_SHORT).show();
        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                initData(20);
                recyclerView.getAdapter().notifyDataSetChanged();
            }
        }, 3000L);
    }


    private class MyAdapter extends RecyclerView.Adapter {
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.item, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            //ImageView avatarImageView = ((MyViewHolder) holder).avatarImageView;
            //avatarImageView.setImageResource(list.get(position));
            final TextView ic_text = ((MyViewHolder) holder).iv_text;
            ic_text.setText(list.get(position).getText());
            ((MyViewHolder) holder).mScrollView.fullScroll(ScrollView.FOCUS_UP);
            /*
            ic_text.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    ic_text.setText(list.get(position)+"行数"+ic_text.getLineCount());
                    if(ic_text.getLineCount()>0){
                        ic_text.getViewTreeObserver()
                                .removeOnGlobalLayoutListener(this);
                    }
                }
            }); */

        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView iv_text;
            ImageView likeImageView;
            ImageView dislikeImageView;
            VerticalScrollview mScrollView;

            MyViewHolder(View itemView) {
                super(itemView);
                iv_text = (TextView) itemView.findViewById(R.id.main_item_text);
                likeImageView = (ImageView) itemView.findViewById(R.id.iv_like);
                dislikeImageView = (ImageView) itemView.findViewById(R.id.iv_dislike);
                mScrollView = (VerticalScrollview) itemView.findViewById(R.id.main_item_scrollveiw);
            }

        }
    }


    public void onClickLike(){
        locallist.clear();
        String allData = load();
        String [] string1= allData.split("#00#");
        for (int i = 0;i < string1.length;i++){
            String [] string2= string1[i].split("#0#");
            Mydata mydata = new Mydata(string2[1],string2[0],string2[2]);
            locallist.add(mydata);
        }

    }


    public void SaveList(Mydata o,boolean isUpdate){
        String data = "";
        if (isUpdate) {
            String 待删除 = o.getId() + "#0#" + o.getType() + "#0#" + o.getText() + "#00#";
            data = load();
            //待删除 = 待删除.replaceAll(o.getId() + "#0#" + o.getType() + "#0#" + o.getText() + "#00#","");
            Log.d("待删除",待删除);
            Log.d("已删除",data);
            data = data.replace(待删除,"");

        }else {
            data = o.getId() + "#0#" + o.getType()
                    + "#0#" + o.getText() + "#00#" + load();
        }
        FileOutputStream out = null;
        BufferedWriter writer = null;
        try {
            out = openFileOutput("datalists", Context.MODE_PRIVATE);
            writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write(data);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null){
                    writer.close();
                }
        } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    public String load(){
        FileInputStream in = null;
        BufferedReader reader = null;
        StringBuffer content = new StringBuffer();
        try {
            in = openFileInput("datalists");
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null){
                content.append(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }

}
