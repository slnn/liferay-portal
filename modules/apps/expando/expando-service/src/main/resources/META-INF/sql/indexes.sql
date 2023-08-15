create index IX_9EB49250 on Expando_ExpandoColumn (tableId, ctCollectionId);
create unique index IX_7DB9468F on Expando_ExpandoColumn (tableId, name[$COLUMN_LENGTH:75$], ctCollectionId);

create index IX_9C7823AC on Expando_ExpandoRow (classPK, ctCollectionId);
create unique index IX_7BCA1CDD on Expando_ExpandoRow (tableId, classPK, ctCollectionId);
create index IX_3BFEDE42 on Expando_ExpandoRow (tableId, ctCollectionId);

create index IX_14159D6D on Expando_ExpandoTable (companyId, classNameId, ctCollectionId);
create unique index IX_88FAC7EC on Expando_ExpandoTable (companyId, classNameId, name[$COLUMN_LENGTH:75$], ctCollectionId);

create index IX_BD9BB87F on Expando_ExpandoValue (classNameId, classPK, ctCollectionId);
create index IX_C937B1EF on Expando_ExpandoValue (columnId, ctCollectionId);
create unique index IX_3B53A44D on Expando_ExpandoValue (columnId, rowId_, ctCollectionId);
create index IX_13AC7888 on Expando_ExpandoValue (rowId_, ctCollectionId);
create index IX_91B213B4 on Expando_ExpandoValue (tableId, classPK, ctCollectionId);
create unique index IX_D50CBDCF on Expando_ExpandoValue (tableId, columnId, classPK, ctCollectionId);
create index IX_C5C00510 on Expando_ExpandoValue (tableId, columnId, ctCollectionId);
create index IX_BDB8898B on Expando_ExpandoValue (tableId, ctCollectionId);
create index IX_82C11FE9 on Expando_ExpandoValue (tableId, rowId_, ctCollectionId);