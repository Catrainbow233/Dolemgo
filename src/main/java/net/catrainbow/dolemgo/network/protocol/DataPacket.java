package net.catrainbow.dolemgo.network.protocol;

import java.util.UUID;

public class DataPacket extends Packet {

    public DataPacket(int id) {
        this.id = id;
        this.buffer = new byte[]{};
        this.key = this.randomKey();
        this.offset = 0;
    }

    private String randomKey() {
        UUID uuid = UUID.randomUUID();
        String str = uuid.toString();
        String uid = str.replace("-", "");
        return uid;
    }

    @Override
    public void encode() {
        StringBuffer code = new StringBuffer();
        this.offset = 0;
        for (char aChar : this.toString().toCharArray()) {
            this.offset++;
            if (offset > 26) this.offset = 0;
            int asciiCode = aChar;
            asciiCode += this.offset;
            char result = (char) asciiCode;
            code.append(result);
        }
        this.buffer = code.toString().getBytes();
    }

    @Override
    public void decode() {
        StringBuilder code = new StringBuilder();
        this.offset = 0;
        char[] chars = this.toString().toCharArray();
        for (char aChar : chars) {
            this.offset++;
            if (offset > 26) this.offset = 0;
            int asciiCode = aChar;
            asciiCode -= this.offset;
            char result = (char) asciiCode;
            code.append(result);
        }
        this.buffer = code.toString().getBytes();
    }

}
