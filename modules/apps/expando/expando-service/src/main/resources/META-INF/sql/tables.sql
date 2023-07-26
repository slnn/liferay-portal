create table Expando_ExpandoColumn (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	columnId LONG not null,
	companyId LONG,
	modifiedDate DATE null,
	tableId LONG,
	name VARCHAR(75) null,
	type_ INTEGER,
	defaultData VARCHAR(75) null,
	typeSettings VARCHAR(75) null,
	primary key (columnId, ctCollectionId)
);

create table Expando_ExpandoRow (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	rowId_ LONG not null,
	companyId LONG,
	modifiedDate DATE null,
	tableId LONG,
	classPK LONG,
	primary key (rowId_, ctCollectionId)
);

create table Expando_ExpandoTable (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	tableId LONG not null,
	companyId LONG,
	classNameId LONG,
	name VARCHAR(75) null,
	primary key (tableId, ctCollectionId)
);

create table Expando_ExpandoValue (
	mvccVersion LONG default 0 not null,
	ctCollectionId LONG default 0 not null,
	valueId LONG not null,
	companyId LONG,
	tableId LONG,
	columnId LONG,
	rowId_ LONG,
	classNameId LONG,
	classPK LONG,
	data_ VARCHAR(75) null,
	primary key (valueId, ctCollectionId)
);