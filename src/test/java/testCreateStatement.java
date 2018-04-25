import java.sql.*;

public class testCreateStatement {
    private static String dbUrl="jdbc:mysql://211.159.219.126:3306/seckill?useUnicode=true&characterEncoding=utf8";
    //用户名
    private static String dbUserName="root";
    //密码
    private static String dbPassword="HanDong85";
    //驱动名称
    private static String jdbcName = "com.mysql.jdbc.Driver";

    static public void main(String argv[]){
        try {
            Class.forName(jdbcName);
            System.out.println("加载驱动成功！");
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("加载驱动失败！");
        }
        Connection con = null;
        try {
            //获取数据库连接
            con = DriverManager.getConnection(dbUrl, dbUserName, dbPassword);
            String sql = "select * from seckill";
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            while(rs.next()){
                System.out.println(rs.getString(1));
            }
            statement = con.createStatement();
            rs = statement.executeQuery(sql);
            while(rs.next()){
                System.out.println(rs.getString(1));
            }
            System.out.println("获取数据库连接成功！");
            System.out.println("进行数据库操作！");
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("获取数据库连接失败！");
        }finally{
            try {
                con.close();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
