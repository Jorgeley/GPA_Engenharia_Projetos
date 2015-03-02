package br.com.gpaengenharia.beans;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.Date;

public class Tarefa implements Comparable, Parcelable {
    private Integer id;
    private String nome;
    private String descricao;
    private Date vencimento;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public Date getVencimento() {
        return vencimento;
    }

    public void setVencimento(Date vencimento) {
        this.vencimento = vencimento;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public boolean equals(Object another) {
        return (this.id == ((Tarefa)another).getId());
    }

    @Override
    public int compareTo(Object o) {
        if (this.id == ((Tarefa)o).getId())
            return 0;
        else return 1;
    }

    public Tarefa(Parcel in) {
        id = in.readByte() == 0x00 ? null : in.readInt();
        nome = in.readString();
        descricao = in.readString();
        long tmpVencimento = in.readLong();
        vencimento = tmpVencimento != -1 ? new Date(tmpVencimento) : null;
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
        dest.writeString(descricao);
        dest.writeLong(vencimento != null ? vencimento.getTime() : -1L);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Tarefa> CREATOR = new Parcelable.Creator<Tarefa>() {
        @Override
        public Tarefa createFromParcel(Parcel in) {
            return new Tarefa(in);
        }

        @Override
        public Tarefa[] newArray(int size) {
            return new Tarefa[size];
        }
    };
}