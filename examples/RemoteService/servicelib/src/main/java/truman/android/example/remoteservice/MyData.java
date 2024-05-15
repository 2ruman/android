package truman.android.example.remoteservice;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class MyData implements Parcelable {

    private final int iData;
    private final long lData;
    private final double dData;
    private final String sData;

    public MyData(int iData, long lData, double dData, String sData) {
        this.iData = iData;
        this.lData = lData;
        this.dData = dData;
        this.sData = sData;
    }

    protected MyData(Parcel in) {
        iData = in.readInt();
        lData = in.readLong();
        dData = in.readDouble();
        sData = in.readString();
    }

    public static final Creator<MyData> CREATOR = new Creator<MyData>() {
        @Override
        public MyData createFromParcel(Parcel in) {
            return new MyData(in);
        }

        @Override
        public MyData[] newArray(int size) {
            return new MyData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(iData);
        dest.writeLong(lData);
        dest.writeDouble(dData);
        dest.writeString(sData);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("I : "); sb.append(iData); sb.append("\n");
        sb.append("L : "); sb.append(lData); sb.append("\n");
        sb.append("D : "); sb.append(dData); sb.append("\n");
        sb.append("S : "); sb.append(sData); sb.append("\n");
        return sb.toString();
    }
}