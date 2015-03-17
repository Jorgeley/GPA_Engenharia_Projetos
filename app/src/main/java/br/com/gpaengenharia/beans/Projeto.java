package br.com.gpaengenharia.beans;

import android.os.Parcel;
import android.os.Parcelable;

public class Projeto implements Comparable, Parcelable {
    private Integer id;
    private String nome;
    private String responsavel;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(String responsavel) {
        this.responsavel = responsavel;
    }

    @Override
    public boolean equals(Object another) {
        return (this.id == ((Projeto)another).getId());
    }

    @Override
    public int compareTo(Object o) {
        if (this.id < ((Projeto)o).getId())
            return -1;
        else if (this.id == ((Projeto)o).getId())
                return 0;
            else
                return 1;
    }

    public Projeto(Parcel in) {
        id = in.readByte() == 0x00 ? null : in.readInt();
        nome = in.readString();
        responsavel = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(id);
        }
        dest.writeString(nome);
        dest.writeString(responsavel);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Projeto> CREATOR = new Parcelable.Creator<Projeto>() {
        @Override
        public Projeto createFromParcel(Parcel in) {
            return new Projeto(in);
        }

        @Override
        public Projeto[] newArray(int size) {
            return new Projeto[size];
        }
    };
}