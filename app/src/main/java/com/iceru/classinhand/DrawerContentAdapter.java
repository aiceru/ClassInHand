package com.iceru.classinhand;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DrawerContentAdapter extends ArrayAdapter<DrawerContent> {

	private LayoutInflater inflater;

	public DrawerContentAdapter(Context context, int textViewResourceId, List<DrawerContent> Objects) {
		super(context, textViewResourceId, Objects);
		this.inflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		DrawerContent drawerContent = this.getItem(position);
		int type = drawerContent.getType();
		switch(type) {
		case DrawerItem.ITEM_TYPE:
			view = getItemView(convertView, parent, drawerContent);
			break;
		case DrawerSubItem.SUBITEM_TYPE:
			view = getSubItemView(convertView, parent, drawerContent);
			break;
		case DrawerSection.SECTION_TYPE:
		default:
			view = getSectionView(convertView, parent, drawerContent);
			break;
		}
		return view;
	}

	public View getItemView(View convertView, ViewGroup parent, DrawerContent drawerContent) {

		DrawerItem drawerItem = (DrawerItem)drawerContent;
		DrawerItemHolder drawerItemHolder = null;

		if(convertView == null) {
			convertView = inflater.inflate(R.layout.drawer_item, parent, false);
			TextView labelView = (TextView)convertView.findViewById(R.id.drawerItem_label);
			ImageView iconView = (ImageView)convertView.findViewById(R.id.drawerItem_icon);

			drawerItemHolder = new DrawerItemHolder();
			drawerItemHolder.labelView = labelView;
			drawerItemHolder.iconView = iconView;

			convertView.setTag(drawerItemHolder);
		}

		if(drawerItemHolder == null) {
			drawerItemHolder = (DrawerItemHolder)convertView.getTag();
		}

		drawerItemHolder.labelView.setText(drawerItem.getLabel());
		drawerItemHolder.iconView.setImageResource(drawerItem.getIcon());

		return convertView;

	}

	public View getSubItemView(View convertView, ViewGroup parent, DrawerContent drawerContent) {

		DrawerSubItem drawerSubItem = (DrawerSubItem)drawerContent;
		DrawerItemHolder drawerItemHolder = null;

		if(convertView == null) {
			convertView = inflater.inflate(R.layout.drawer_subitem, parent, false);
			TextView labelView = (TextView)convertView.findViewById(R.id.drawerSubItem_label);
			ImageView iconView = (ImageView)convertView.findViewById(R.id.drawerSubItem_icon);

			drawerItemHolder = new DrawerItemHolder();
			drawerItemHolder.labelView = labelView;
			drawerItemHolder.iconView = iconView;

			convertView.setTag(drawerItemHolder);
		}

		if(drawerItemHolder == null) {
			drawerItemHolder = (DrawerItemHolder)convertView.getTag();
		}

		drawerItemHolder.labelView.setText(drawerSubItem.getLabel());
		drawerItemHolder.iconView.setImageResource(drawerSubItem.getIcon());

		return convertView;

	}

	public View getSectionView(View convertView, ViewGroup parent, DrawerContent drawerContent) {

		DrawerSection drawerSection = (DrawerSection)drawerContent;
		DrawerSectionHolder drawerSectionHolder = null;

		if(convertView == null) {
			convertView = inflater.inflate(R.layout.drawer_section, parent, false);
			TextView labelView = (TextView)convertView.findViewById(R.id.drawerSection_label);

			drawerSectionHolder = new DrawerSectionHolder();
			drawerSectionHolder.labelView = labelView;

			convertView.setTag(drawerSectionHolder);
		}

		if(drawerSectionHolder == null) {
			drawerSectionHolder = (DrawerSectionHolder)convertView.getTag();
		}

		drawerSectionHolder.labelView.setText(drawerSection.getLabel());	

		return convertView;

	}

	@Override
	public int getViewTypeCount() {
		return 3;
	}

	@Override
	public int getItemViewType(int position) {
		return this.getItem(position).getType();
	}

	@Override
	public boolean isEnabled(int position) {
		return getItem(position).isEnabled();
	}

	private static class DrawerItemHolder {
		private TextView labelView;
		private ImageView iconView;
	}

	private class DrawerSectionHolder {
		private TextView labelView;
	}
}
