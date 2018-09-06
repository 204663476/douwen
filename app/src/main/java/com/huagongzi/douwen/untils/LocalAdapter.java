package com.huagongzi.douwen.untils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.huagongzi.douwen.R;

import java.util.List;

/**
 * Created by 许鑫源 on 2018/8/11.
 */


public class LocalAdapter extends ArrayAdapter<Mydata> {
    private int resourceId;
    public LocalAdapter(Context context, int textViewResourceId, List<Mydata> objects){
        //三个参数依次是上下文、ListView子项布局id、要适配的数据
        super(context,textViewResourceId,objects);
        resourceId = textViewResourceId;
    }

    //重写getView方法，自定义apapter的表现
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Mydata mydata = getItem(position); //获取当前项的Mydata实例
        View view;
        ViewHolder viewHolder;

        //判断convertView是否已经存在，如果为null，则使用LayoutInflater去加载布局；
        //如果不为null，则直接对convertView进行重用。这样就能避免重复重建，大大提高ListView的运行效率，提高性能。

        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);

               /*将ViewHolder存储在View中,用来存储Text实例，
               避免每次都调用findViewById来获取这两个实例，提高性能*/
            viewHolder = new ViewHolder();
            viewHolder.Text=(TextView)view.findViewById(R.id.list_item_text);
            //通过View.setTag()方法，将ViewHolder存储到View中，像打标签
            view.setTag(viewHolder);
        }else {
            view = convertView;
            //从View中取出ViewHolder
            viewHolder = (ViewHolder)view.getTag();
        }
        viewHolder.Text.setText(mydata.getText());
        return view;
    }
}

class ViewHolder{
    TextView Text;
}

