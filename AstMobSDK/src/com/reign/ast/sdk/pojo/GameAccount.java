package com.reign.ast.sdk.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.reign.ast.sdk.util.GameUtil;

/**
 * 游戏账号
 * 
 * @author zhouwenjia
 *
 */
public class GameAccount implements Parcelable {

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public static final int QUICK = 0;
	public static final int COMMON = 1;
	public static final int PHONE = 2;

	public int type;
	public String name;
	public String pwd;
	public String token;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public boolean isEncryptPwd = true;

	/**
	 * 构造函数
	 */
	public GameAccount() {

	}

	/**
	 * 构造函数
	 * 
	 * @param type
	 * @param name
	 * @param pwd
	 */
	public GameAccount(int type, String name, String pwd, String token) {
		this.type = type;
		this.name = GameUtil.addSuffix(name);
		this.pwd = pwd;
		this.token = token;
	}

	/**
	 * 构造函数
	 * 
	 * @param type
	 */
	public GameAccount(int type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return GameUtil.getAccountInfo(this.type, this.name, this.pwd,
				this.token);
	}

	@Override
	public int hashCode() {
		int prime = 31;
		int result = 1;
		result = prime * result
				+ (this.name == null ? 0 : this.name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (null == obj) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		GameAccount other = (GameAccount) obj;
		if (null == this.name) {
			if (null != other.name) {
				return false;
			}
		} else if (!this.name.equals(other.name)) {
			return false;
		}
		return true;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int arg1) {
		// TODO Auto-generated method stub
		parcel.writeInt(type);
		parcel.writeString(name);
		parcel.writeString(pwd);
		parcel.writeString(token);
	}

	public GameAccount(Parcel in) {
		type = in.readInt();
		name = in.readString();
		pwd = in.readString();
		token = in.readString();
	}

	public static final Parcelable.Creator<GameAccount> CREATOR = new Parcelable.Creator<GameAccount>() {
		@Override
		public GameAccount createFromParcel(Parcel arg0) {
			// TODO Auto-generated method stub
			return new GameAccount(arg0);
		}

		@Override
		public GameAccount[] newArray(int arg0) {
			// TODO Auto-generated method stub
			return new GameAccount[arg0];
		}

	};

}
