# PL/SQL

## 打印hello,world

DBMS_output.put_line('hello,world');

- F8执行
- cmd：sqlplus【set serveroutput on】执行begin,end;反斜杠结束

## 变量

直接赋值：

v_name:='zhangsan'

语句赋值：

select...into...

例如：

### 普通变量

demo1:

```plsql
--打印人员的信息，包括：姓名，薪水，地址

DECLARE

--姓名

V_NAME VARCHAR2(20) := '张三';

--薪水

V_SAL NUMBER;

--地址

V_ADDRESS VARCHAR(200);

BEGIN

--程序中直接赋值

V_SAL:=1999;

--语句赋值

SELECT '联想科技有限公司' INTO V_ADDR FROM DUAL;

--打印变量

DBMS_OUTPUT.PUT_LINE('姓名：'||V_NAME||",薪水："||V_SAL||",地址："||V_ADDR)

END

```

demo2:

```plsql
查询emp表中2222编号的员工共的个人信息，打印姓名和薪水

DECLARE

--姓名

V_NAME VARCHAR(11);

--薪水

V_SAL NUMBER;

BEGIN

SELECT ENAME,SAL INFO V_NAME,V_SAL FROM EMP WHERE EMPNO=2222;

--打印输出

DBMS_OUTPUT.PUT_LINE(...);

END;
```

demo2存在问题：

生命变量的类型、长度是否能兼容数据库字段的类型和长度？如果不能则会报错！

demo2的改进：

### 引用型变量

table.field%type使得变量类型和长度和数据库字段的类型和长度保持一致

```plsql
查询emp表中2222编号的员工共的个人信息，打印姓名和薪水

DECLARE

--姓名

V_NAME emp.ename%type;

--薪水

V_SAL emp.sal%type;

BEGIN

SELECT ENAME,SAL INFO V_NAME,V_SAL FROM EMP WHERE EMPNO=2222;

--打印输出

DBMS_OUTPUT.PUT_LINE(...);

END;
```

### 记录型变量

v_emp emp$rowtype中v_emp代表emp表中的一条数据;

慎用：查询的是*，在查询字段比较大的时候使用，否则效率低下

```plsql
查询emp表中2222编号的员工共的个人信息，打印姓名和薪水

DECLARE

--一条记录

V_emp emp%rowtype;

BEGIN

SELECT * INTO V_EMP FROM EMP WHERE EMPNO=2222;

--打印输出

DBMS_OUTPUT.PUT_LINE(...v_emp.ename...v_emp.sal);

END;
```

## 流程控制

### 条件分支

```plsql
BEGIN

​	IF 条件1 THEN 执行1

​		ELSIF 条件2 THEN 执行2

​		ELSE 执行3

​	END IF;

END;
```

demo1:

```plsql
DECLARE V_COUNT NUMBER;
BEGIN
	SELECT COUNT(1) INFO V_COUNT FROM EMP;
	
	IF V_COUNT>20 THEN DBMS_OUTPUT.PUT_LINE();
	ELSIF V>10    THEN DBMS_OUTPUT.PUT_LINE();
	ELSE               DBMS_OUTPUT.PUT_LINE();
	END IF;
END;
```

### 循环

```plsql
DECLARE
	V_NUM NUMBER:=1;
BEGIN
	LOOP
		EXIT WHEN V_NUM>10;
		DBMS_OUTPUT.PUT_LINE(V_NUM);
		V_NUM:=V_NUM+1;
	END LOOP;
END;
```

### 游标

#### 无参数

```plsql
DECLARE
    --IS游标声明
	CURSOR C_EMP IS SELECT ENAME,ESAL FROM EMP;
	V_ENAME EMP.ENAME%TYPE;
	V_SAL EMP.ESAL%TYPE;
BEGIN
	--打开游标
	OPEN C_EMP
		LOOP
			--通过%NOTFOUND判断是否有值，有则打印否则推出
			EXIT WHEN C_EMP%NOTFOUND;
			--通过FETCH语句获取游标中的值赋值给变量
			FETCH C_EMP INTO V_NAME,V_SAL;
			DBMS_OUTPUT.PUT_LINE();
		END LOOP;
	--关闭游标
	CLOSE C_EMP;
END;
```

#### 有参数

```plsql
DECLARE
    --IS游标声明
	CURSOR C_EMP(V_DEPTNO EMP.DEPTNO%TYPE) IS SELECT ENAME,ESAL FROM EMP WHERE DEPTNO=V_DEPTNO;
	V_ENAME EMP.ENAME%TYPE;
	V_SAL EMP.ESAL%TYPE;
BEGIN
	--打开游标
	OPEN C_EMP(10)
		LOOP
			--通过FETCH语句获取游标中的值赋值给变量
			--先FETCH,在EXIT WHEN，否则可能重复输出。游标默认有值，需要先FETCH好了之后才能知道是否真正有值
			FETCH C_EMP INTO V_NAME,V_SAL;
			
			--通过%NOTFOUND判断是否有值，有则打印否则推出
			EXIT WHEN C_EMP%NOTFOUND;
			
			DBMS_OUTPUT.PUT_LINE();
		END LOOP;
	--关闭游标
	CLOSE C_EMP;
END;
```

# 存储过程

创建：

```plsql
create or replace procedure p_hello is
--声明变量
begin
...
end p_hello;
```

调用1：

```plsql
begin
	p_hello;
end;
```

调用2：

SQL>exec p_hello;

【set serveroutput on】

## 带有入参：

```plsql
create or replace procedure query_something(i_empno in emp.empno%type) is
e_ename emp.ename%type;
e_sal   emp.sal%type;
begin
	select ename,sal into e_ename,e_sal from emp where empno=i_empno;
	dbms_output.put_line();
end query_something
```

```plsql
declare
	i 	integer:=2222;
begin
	query_something(i);
end;
```

```plsql
SQL>exec query_something(2222);
```

## 带有入参和出参数

```plsql
create or replace procedure query_something(i_empno in emp.empno%type,o_ename emp.ename%type) is
begin
	select ename into o_ename from emp where empno=i_empno;
end query_something
```

```plsql
declare
	e_ename emp.ename%type;
begin
	query_something(2222,e_ename);
	dbms_output.put_line(e_ename);
end;
```

## java执行存储过程

maven网站下载ojdbc6导入idea的lib文件夹，add as library;

```java
import oracle.jdbc.oracore.OracleType;
import java.sql.*;

public class Test {
    public static void main(String[] args) {
        Connection conn = null;
        CallableStatement call = null;
        try {
            //加载驱动
            Class.forName("oracle.jdbc.driver.OracleDriver");

            //获取连接
            conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:chiszc", ",", "");

            //获得语句对象
            call = conn.prepareCall("{call p_query(?,?)}");

            //设置输入参数值
            call.setInt(1, 1111);

            //注册输出参数
            call.registerOutParameter(2, OracleType.STYLE_DOUBLE);

            //执行call语句
            call.execute();

            //获取输出参数
            double res = call.getDouble(2);

            System.out.println(res);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            if (call != null) {
                try {
                    call.close();

                } catch (Exception exception) {
                    System.out.println(exception.getMessage());
                }
            }
            if (conn != null) {
                try {
                    conn.close();

                } catch (Exception exception) {
                    System.out.println(exception.getMessage());
                }
            }
        }
    }
}
```































