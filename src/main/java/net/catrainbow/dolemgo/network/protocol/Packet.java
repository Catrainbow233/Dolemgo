package net.catrainbow.dolemgo.network.protocol;

public abstract class Packet {

    public int id;
    public String key;
    public byte[] buffer;
    public int offset;

    public void encode() {
    }

    public void decode() {
    }

    public void clear() {
        this.buffer = new byte[]{};
        this.offset = 0;
    }

    public int length() {
        return this.buffer.length;
    }

    public void putString(String string) {
        StringBuilder stringBuilder = new StringBuilder(this.toString());
        this.buffer = stringBuilder.append("|").append(string).toString().getBytes();
    }

    @Override
    public String toString() {
        return new String(buffer);
    }

    public byte[] toByte() {
        return this.buffer;
    }

}
