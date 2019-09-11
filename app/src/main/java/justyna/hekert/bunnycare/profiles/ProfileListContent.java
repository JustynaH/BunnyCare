package justyna.hekert.bunnycare.profiles;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileListContent {

    public static final List<Profile> ITEMS = new ArrayList<Profile>();

    public static final Map<String, Profile> ITEM_MAP = new HashMap<String, Profile>();

    public static Profile getItem(int position) {
        return ITEMS.get(position);
    }

    public static void addItem(Profile item) {
        ITEMS.add(item);
        String id = Long.toString((item.id));
        ITEM_MAP.put(id, item);
    }

    public static long modItem(int position, Profile item){
        ITEMS.set(position, item);
        return ITEMS.get(position).id;
    }

    public static long removeItem(int position) {
        long itemId = ITEMS.get(position).id;
        ITEMS.remove(position);
        ITEM_MAP.remove(itemId);
        return itemId;
    }

    public static void clearList() {
        ITEMS.clear();
        ITEM_MAP.clear();
    }

    public static class Profile implements Parcelable {
        public long id;
        public String name;
        public String dateOfBirth;
        public Double weight;
        public String sex;
        public String type;
        public String specChar;
        public String picPath;

        public Profile(long id, String name, String dateOfBirth, Double weight, String sex, String type, String specialChar, String picPath) {
            this.id = id;
            this.name = name;
            this.dateOfBirth = dateOfBirth;
            this.weight = weight;
            this.sex = sex;
            this.type = type;
            this.specChar = specialChar;
            this.picPath = picPath;
        }

        protected Profile(Parcel in) {
            id = in.readLong();
            name = in.readString();
            dateOfBirth = in.readString();
            if (in.readByte() == 0) {
                weight = null;
            } else {
                weight = in.readDouble();
            }
            sex = in.readString();
            type = in.readString();
            specChar = in.readString();
            picPath = in.readString();
        }

        public void setPicPath(String path) {
            this.picPath = path;
        }

        public static final Creator<Profile> CREATOR = new Creator<Profile>() {
            @Override
            public Profile createFromParcel(Parcel in) {
                return new Profile(in);
            }

            @Override
            public Profile[] newArray(int size) {
                return new Profile[size];
            }
        };

        @Override
        public String toString() {
            return name;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(id);
            dest.writeString(name);
            dest.writeString(dateOfBirth);
            if (weight == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(weight);
            }
            dest.writeString(sex);
            dest.writeString(type);
            dest.writeString(specChar);
            dest.writeString(picPath);
        }
    }
}
