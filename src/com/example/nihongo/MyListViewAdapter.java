package com.example.nihongo;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MyListViewAdapter extends BaseAdapter{

	private List<WordBean> beanList;
	private LayoutInflater layoutInflater;
	public int clickPosition = -1;

	public MyListViewAdapter(Context ctx, List<WordBean> beanList) {
		this.beanList = beanList;
		this.layoutInflater = LayoutInflater.from(ctx);
	}

	@Override
	public int getCount() {
		if (beanList != null) {
			return beanList.size();
		} else {
			return -1;
		}
	}

	@Override
	public Object getItem(int position) {
		if (beanList != null) {
			return beanList.get(position);
		} else {
			return null;
		}
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = layoutInflater.inflate(R.layout.listview_item,
					null);
			holder.tv_jiaming = (TextView) convertView.findViewById(R.id.tv_jiaming);
			holder.ll_hide = (LinearLayout)convertView.findViewById(R.id.ll_hide);
			holder.tv_hanzi = (TextView) convertView.findViewById(R.id.tv_hanzi);
			holder.tv_shiyi = (TextView) convertView.findViewById(R.id.tv_shiyi);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.tv_jiaming.setText(beanList.get(position).jiaming);
		holder.tv_hanzi.setText(beanList.get(position).hanzi);
		holder.tv_shiyi.setText(beanList.get(position).fanyi);
		if (clickPosition == position) {//当条目为刚才点击的条目时
			if (holder.ll_hide.isSelected()) {//当条目状态图标为选中时，说明该条目处于展开状态，此时让它隐藏，并切换状态图标的状态。
				holder.ll_hide.setSelected(false);
				holder.ll_hide.setVisibility(View.GONE);
				clickPosition=-1;//隐藏布局后需要把标记的position去除掉，否则，滑动listview让该条目划出屏幕范围，
                // 当该条目重新进入屏幕后，会重新恢复原来的显示状态。经过打log可知每次else都执行一次 （条目第二次进入屏幕时会在getview中寻找他自己的状态，相当于重新执行一次getview） 
                //因为每次滑动的时候没标记得position填充会执行click
			}else{//当状态条目处于未选中时，说明条目处于未展开状态，此时让他展开。同时切换状态图标的状态。
				holder.tv_jiaming.setSelected(true);
				holder.ll_hide.setVisibility(View.VISIBLE);
			}
		}else {//当填充的条目position不是刚才点击所标记的position时，让其隐藏，状态图标为false。
            //每次滑动的时候没标记得position填充会执行此处，把状态改变。所以如果在以上的if (vh.selectorImg.isSelected()) {}中不设置clickPosition=-1；则条目再次进入屏幕后，还是会进入clickposition==position的逻辑中
            //而之前的滑动（未标记条目的填充）时，执行此处逻辑，已经把状态图片的selected置为false。所以上面的else中的逻辑会在标记过的条目第二次进入屏幕时执行，如果之前的状态是显示，是没什么影响的，再显示一次而已，用户看不出来，但是如果是隐藏状态，就会被重新显示出来
			holder.ll_hide.setVisibility(View.GONE);
            holder.tv_jiaming.setSelected(false);
        }
		
		holder.ll_hide.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.d("MyListViewAdapter","点击了汉字,释义部分");
				
			}
		});
		holder.tv_jiaming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickPosition = position;//记录点击的position
                notifyDataSetChanged();//刷新adapter重新填充条目。在重新填充的过程中，被记录的position会做展开或隐藏的动作，具体的判断看上面代码
           //在此处需要明确的一点是，当adapter执行刷新操作时，整个getview方法会重新执行，也就是条目重新做一次初始化被填充数据。
                //所以标记position，不会对条目产生影响，执行刷新后 ，条目重新填充当，填充至所标记的position时，我们对他处理，达到展开和隐藏的目的。
             //明确这一点后，每次点击代码执行逻辑就是 onclick（）---》getview（）
            }
        });
		
		return convertView;
	}

	private final class ViewHolder {
		public TextView tv_jiaming;
		public LinearLayout ll_hide;
		public TextView tv_hanzi;
		public TextView tv_shiyi;
	}

	/**
	 * 将此adapter的成员变量beanList重置为给定的
	 * 
	 * @param beanList
	 */
	public void resetBeanList(List<WordBean> beanList) {
		this.beanList = beanList;
	}

}
