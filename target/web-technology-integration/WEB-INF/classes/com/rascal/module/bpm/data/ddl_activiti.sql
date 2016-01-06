CREATE VIEW ACT_ID_USER AS 
SELECT
auth_user.authUid AS ID_,
0 AS REV_,
null AS FIRST_,
null AS LAST_,
auth_user.email AS EMAIL_,
auth_user.password AS PWD_,
null AS PICTURE_ID_
FROM
auth_user;

CREATE VIEW ACT_ID_MEMBERSHIP AS 
SELECT
auth_user.authUid AS USER_ID_,
auth_role.code AS GROUP_ID_
FROM
auth_role,auth_user,auth_user_r2_role
WHERE auth_role.id=auth_user_r2_role.role_id
and auth_user.id=auth_user_r2_role.user_id;

CREATE VIEW ACT_ID_GROUP AS 
SELECT
auth_role.code AS ID_,
0 AS REV_,
auth_role.name AS NAME_,
'role' AS TYPE_
FROM
auth_role