/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.mail.reader.model.impl;

import com.liferay.mail.reader.model.Account;
import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CacheModel;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The cache model class for representing Account in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@ProviderType
public class AccountCacheModel implements CacheModel<Account>, Externalizable {

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof AccountCacheModel)) {
			return false;
		}

		AccountCacheModel accountCacheModel = (AccountCacheModel)obj;

		if (accountId == accountCacheModel.accountId) {
			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return HashUtil.hash(0, accountId);
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(53);

		sb.append("{accountId=");
		sb.append(accountId);
		sb.append(", companyId=");
		sb.append(companyId);
		sb.append(", userId=");
		sb.append(userId);
		sb.append(", userName=");
		sb.append(userName);
		sb.append(", createDate=");
		sb.append(createDate);
		sb.append(", modifiedDate=");
		sb.append(modifiedDate);
		sb.append(", address=");
		sb.append(address);
		sb.append(", personalName=");
		sb.append(personalName);
		sb.append(", protocol=");
		sb.append(protocol);
		sb.append(", incomingHostName=");
		sb.append(incomingHostName);
		sb.append(", incomingPort=");
		sb.append(incomingPort);
		sb.append(", incomingSecure=");
		sb.append(incomingSecure);
		sb.append(", outgoingHostName=");
		sb.append(outgoingHostName);
		sb.append(", outgoingPort=");
		sb.append(outgoingPort);
		sb.append(", outgoingSecure=");
		sb.append(outgoingSecure);
		sb.append(", login=");
		sb.append(login);
		sb.append(", password=");
		sb.append(password);
		sb.append(", savePassword=");
		sb.append(savePassword);
		sb.append(", signature=");
		sb.append(signature);
		sb.append(", useSignature=");
		sb.append(useSignature);
		sb.append(", folderPrefix=");
		sb.append(folderPrefix);
		sb.append(", inboxFolderId=");
		sb.append(inboxFolderId);
		sb.append(", draftFolderId=");
		sb.append(draftFolderId);
		sb.append(", sentFolderId=");
		sb.append(sentFolderId);
		sb.append(", trashFolderId=");
		sb.append(trashFolderId);
		sb.append(", defaultSender=");
		sb.append(defaultSender);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public Account toEntityModel() {
		AccountImpl accountImpl = new AccountImpl();

		accountImpl.setAccountCacheModel(this);

		return accountImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		accountId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();
		address = objectInput.readUTF();
		personalName = objectInput.readUTF();
		protocol = objectInput.readUTF();
		incomingHostName = objectInput.readUTF();

		incomingPort = objectInput.readInt();

		incomingSecure = objectInput.readBoolean();
		outgoingHostName = objectInput.readUTF();

		outgoingPort = objectInput.readInt();

		outgoingSecure = objectInput.readBoolean();
		login = objectInput.readUTF();
		password = objectInput.readUTF();

		savePassword = objectInput.readBoolean();
		signature = objectInput.readUTF();

		useSignature = objectInput.readBoolean();
		folderPrefix = objectInput.readUTF();

		inboxFolderId = objectInput.readLong();

		draftFolderId = objectInput.readLong();

		sentFolderId = objectInput.readLong();

		trashFolderId = objectInput.readLong();

		defaultSender = objectInput.readBoolean();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeLong(accountId);

		objectOutput.writeLong(companyId);

		objectOutput.writeLong(userId);

		if (userName == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(userName);
		}

		objectOutput.writeLong(createDate);
		objectOutput.writeLong(modifiedDate);

		if (address == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(address);
		}

		if (personalName == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(personalName);
		}

		if (protocol == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(protocol);
		}

		if (incomingHostName == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(incomingHostName);
		}

		objectOutput.writeInt(incomingPort);

		objectOutput.writeBoolean(incomingSecure);

		if (outgoingHostName == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(outgoingHostName);
		}

		objectOutput.writeInt(outgoingPort);

		objectOutput.writeBoolean(outgoingSecure);

		if (login == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(login);
		}

		if (password == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(password);
		}

		objectOutput.writeBoolean(savePassword);

		if (signature == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(signature);
		}

		objectOutput.writeBoolean(useSignature);

		if (folderPrefix == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(folderPrefix);
		}

		objectOutput.writeLong(inboxFolderId);

		objectOutput.writeLong(draftFolderId);

		objectOutput.writeLong(sentFolderId);

		objectOutput.writeLong(trashFolderId);

		objectOutput.writeBoolean(defaultSender);
	}

	public long accountId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public long modifiedDate;
	public String address;
	public String personalName;
	public String protocol;
	public String incomingHostName;
	public int incomingPort;
	public boolean incomingSecure;
	public String outgoingHostName;
	public int outgoingPort;
	public boolean outgoingSecure;
	public String login;
	public String password;
	public boolean savePassword;
	public String signature;
	public boolean useSignature;
	public String folderPrefix;
	public long inboxFolderId;
	public long draftFolderId;
	public long sentFolderId;
	public long trashFolderId;
	public boolean defaultSender;

}