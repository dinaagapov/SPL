package bgu.spl.mics.application.objects;

import com.google.gson.annotations.SerializedName;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Data {
    /**
     * Enum representing the Data type.
     */
    public enum Type {
        @SerializedName("images")
        Images,
        @SerializedName("Images")
        images,
        @SerializedName("Text")
        Text,
        @SerializedName("Tabular")
        Tabular
    }

    private Type type;
    private transient int processed;
    private int size;

    public Data(Type type , int size){
        this.type = type;
        this.size = size;
        processed = 0;
    }
    public Type getType() {
        return type;
    }

    public int getProcessed() {
        return processed;
    }

    public int getSize() {
        return size;
    }

    public void setProcessed(int processed) {
        this.processed = processed;
    }
    @Override
    public String toString() {
        return "Data{" +
                "type=" + type + "\n" +
                ", size=" + size +  "\n" +
                '}';
    }

}
