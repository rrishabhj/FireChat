//package com.google.firebase.udacity.friendlychat.adaptor;
//
//import android.app.Activity;
//import android.content.Context;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.bumptech.glide.Glide;
//import com.google.firebase.udacity.friendlychat.R;
//import com.google.firebase.udacity.friendlychat.model.FriendlyMessage;
//import com.google.firebase.udacity.friendlychat.model.User;
//
//import java.util.List;
//import java.util.List;
//
//import static java.security.AccessController.getContext;
//
///**
// * Created by DMI on 20-06-2017.
// */
//
//public class UserAdaptor extends ArrayAdapter<User> {
//
//
//    public UserAdaptor(Context context, int resource, List<User> objects) {
//        super(context, resource, objects);
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        if (convertView == null) {
//            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_message, parent, false);
//        }
//
//        ImageView photoImageView = (ImageView) convertView.findViewById(R.id.photoImageView);
//        TextView messageTextView = (TextView) convertView.findViewById(R.id.messageTextView);
//        TextView authorTextView = (TextView) convertView.findViewById(R.id.nameTextView);
//
//        User user = getItem(position);
//
////        boolean isPhoto = message.getPhotoUrl() != null;
////        if (isPhoto) {
////            messageTextView.setVisibility(View.GONE);
////            photoImageView.setVisibility(View.VISIBLE);
////            Glide.with(photoImageView.getContext())
////                    .load(message.getPhotoUrl())
////                    .into(photoImageView);
////        } else {
//            messageTextView.setVisibility(View.VISIBLE);
//            photoImageView.setVisibility(View.GONE);
//            messageTextView.setText(user.getName());
////        }
//        authorTextView.setText(user.getEmail());
//
//        return convertView;
//    }
//}
