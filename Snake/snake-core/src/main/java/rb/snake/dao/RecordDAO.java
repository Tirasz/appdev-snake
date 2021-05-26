package rb.snake.dao;

import rb.snake.model.Record;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;


public class RecordDAO extends DAO<Record>  {

    private static final String SELECT_SP_RECORD = "SELECT * FROM RECORDS WHERE player1 = ? AND player2 IS NULL";
    private static final String SELECT_ALL_SP = "SELECT * FROM RECORDS WHERE player2 IS NULL";
    private static final String SELECT_MP_RECORD = "SELECT * FROM RECORDS WHERE ((player1 = ? AND player2 = ?) OR (player1 = ? AND player2 = ?))";
    private static final String SELECT_ALL_MP = "SELECT * FROM RECORDS WHERE player2 IS NOT NULL";
    private static final String SELECT_ALL_BY_NAME = "SELECT * FROM RECORDS WHERE instr(player1, ?) > 0 OR instr(player2, ?) > 0;";
    private static final String INSERT_RECORD = "INSERT INTO RECORDS VALUES(?, ?, ?, ?)";
    private static final String UPDATE_SP_RECORD = "UPDATE RECORDS set player1 = ?, p1Score = ? WHERE (player1 = ? AND player2 IS NULL)";
    private static final String UPDATE_MP_RECORD = "UPDATE RECORDS set player1 = ?, player2 = ?, p1Score = ?, p2Score = ? WHERE ((player1 = ? AND player2 = ?) OR (player1 = ? AND player2 = ?))";
    private static final String SELECT_ALL_RECORDS = "SELECT * FROM RECORDS";
    private static final String DELETE_SP_RECORD = "DELETE FROM RECORDS WHERE (player1 = ? AND player2 IS NULL)";
    private static final String DELETE_MP_RECORD = "DELETE FROM RECORDS WHERE ((player1 = ? AND player2 = ?) OR (player1 = ? AND player2 = ?))";



    public RecordDAO() {
        super();
    }


    public Record getRecord(String p1){
        return getSPRecord(p1);
    }

    public Record getRecord(String p1, String p2){
        return getMPRecord(p1,p2);
    }

    public Record save(String p1, int p1Score){
        return saveSPRecord(p1, p1Score);
    }

    public Record save(String p1, int p1Score, String p2, int p2Score){
        return saveMPRecord(p1, p1Score, p2, p2Score);
    }

    public Record delete(String p1){
        return deleteSPRecord(p1);
    }

    public Record delete(String p1, String p2){
        return deleteMPRecord(p1, p2);
    }

    @Override
    public List<Record> getAll() {
        return getAll(SELECT_ALL_RECORDS);
    }

    @Override
    public Record save(Record record) {
        if(record.getP2() == null){

            return saveSPRecord(record.getP1(), record.getP1Score());
        }else{
            return saveMPRecord(record.getP1(),record.getP1Score(), record.getP2(), record.getP2Score());
        }
    }

    @Override
    public Record delete(Record record) {
        if(record.getP2() == null){
            return deleteSPRecord(record.getP1());
        }else{
            return deleteMPRecord(record.getP1(), record.getP2());
        }
    }

    public List<Record> searchByName(String name){
        List<Record> records = new ArrayList<>();
        try(PreparedStatement stm = DBC.prepareStatement(SELECT_ALL_BY_NAME)){
            //String like = "%" + name + "%";
            stm.setString(1,name);
            stm.setString(2,name);
            ResultSet results = stm.executeQuery();
            while(results.next()){
                records.add(initRecord(results));
            }
            TimeUnit.SECONDS.sleep(1);
        } catch (SQLException | InterruptedException throwables) {
            throwables.printStackTrace();
        }

        return records;
    }

    public List<Record> getAllMP(){
        return getAll(SELECT_ALL_MP);
    }

    public List<Record> getAllSP(){
        return getAll(SELECT_ALL_SP);
    }

    private List<Record> getAll(String QUERY) {
        List<Record> records = new ArrayList<>();

        try(Statement stm = DBC.createStatement()){
            ResultSet results = stm.executeQuery(QUERY);
            while(results.next()){
                records.add(initRecord(results));
            }
            TimeUnit.SECONDS.sleep(1);
        } catch (SQLException | InterruptedException throwables) {
            throwables.printStackTrace();
        }
        return records;
    }

    private Record initRecord(ResultSet result) throws SQLException{
        Record record = new Record();
        record.setP1(result.getString("player1"));
        record.setP2(result.getString("player2"));
        record.setP1Score(result.getInt("p1Score"));
        record.setP2Score(result.getInt("p2Score"));
        return record;
    }

    private Record getSPRecord(String player){
        //Returns null if the player doesn't have a SP record!
        Record record = null;
        System.out.println("Looking for sp record for: "+player);
        try(PreparedStatement stm = DBC.prepareStatement(SELECT_SP_RECORD)){
            stm.setString(1,player);
            ResultSet result = stm.executeQuery();
            if(result.next()){
                System.out.println("found sp record:");
                record = initRecord(result);
                System.out.println(record);
            }
            TimeUnit.SECONDS.sleep(1);
        } catch (SQLException | InterruptedException throwables) {
            throwables.printStackTrace();
        }

        return record;
    }

    private Record getMPRecord(String p1, String p2){
        Record record = null;
        System.out.println("Looking for mp record: "+p1+" vs "+p2);
        try(PreparedStatement stm = DBC.prepareStatement(SELECT_MP_RECORD)){
            stm.setString(1, p1);
            stm.setString(2, p2);
            stm.setString(3,p2);
            stm.setString(4,p1);
            ResultSet result = stm.executeQuery();
            if(result.next()){
                System.out.println("Found mp record: ");
                record = initRecord(result);
                System.out.println(record);
            }
            TimeUnit.SECONDS.sleep(1);
        } catch (SQLException | InterruptedException throwables) {
            throwables.printStackTrace();
        }
        return record;
    }

    private Record saveSPRecord(String player, int score){
        Record temp = getSPRecord(player);
        PreparedStatement stm;
        try{
            if(temp == null){
                System.out.println("Inserting new SP record for "+player+ " with score: "+score);
                stm = DBC.prepareStatement(INSERT_RECORD);
                stm.setString(1, player);
                stm.setNull(2, java.sql.Types.NULL);
                stm.setInt(3, score);
                stm.setNull(4, java.sql.Types.NULL);
            }else{
                System.out.println("Updating SP record for: "+player+" with score: "+temp.getP1Score()+" -> "+score);
                stm = DBC.prepareStatement(UPDATE_SP_RECORD);
                stm.setString(1, player);
                stm.setInt(2, score);
                stm.setString(3, player);
            }
            stm.executeUpdate();
            stm.close();
            temp = getSPRecord(player);
            TimeUnit.SECONDS.sleep(1);
        } catch (SQLException | InterruptedException throwables) {
            throwables.printStackTrace();
        }
        return temp;
    }

    private Record saveMPRecord(String p1, int p1Score, String p2, int p2Score){
        //Inserts or updates a MP record.
        //P1 and P2 might switch places, which shouldn't matter in a MP record.

        Record temp = getMPRecord(p1,p2);
        PreparedStatement stm;
        try{
            if(temp == null){
                System.out.println("Inserting new MP record for "+p1+ " and "+p2+" with scores: "+p1Score + " and "+p2Score);
                stm = DBC.prepareStatement(INSERT_RECORD);
                stm.setString(1, p1);
                stm.setString(2, p2);
                stm.setInt(3, p1Score);
                stm.setInt(4, p2Score);
            }else{
                System.out.println("Updating MP record with players: "+temp.getP1()+" vs "+temp.getP2());
                stm = DBC.prepareStatement(UPDATE_MP_RECORD);
                stm.setString(1, p1);
                stm.setString(2, p2);
                stm.setInt(3, p1Score);
                stm.setInt(4, p2Score);
                stm.setString(5, p1);
                stm.setString(6, p2);
                stm.setString(7,p2);
                stm.setString(8, p1);
            }
            stm.executeUpdate();
            stm.close();
            temp = getMPRecord(p1, p2);
            TimeUnit.SECONDS.sleep(1);
        } catch (SQLException | InterruptedException throwables) {
            throwables.printStackTrace();
        }
        return temp;
    }

    private Record deleteSPRecord(String p1){
        //Returns null on failure, otherwise returns the deleted record
        Record temp = getSPRecord(p1);
        if(temp == null){
            System.out.println("No SP records to delete for: "+p1);
            return null;
        }
        try(PreparedStatement stm = DBC.prepareStatement(DELETE_SP_RECORD)){
            stm.setString(1, p1);
            stm.executeUpdate();
            System.out.println("Deleted SP record for: "+p1);
            TimeUnit.SECONDS.sleep(1);
        } catch (SQLException | InterruptedException throwables) {
            throwables.printStackTrace();
        }

        return temp;
    }

    private Record deleteMPRecord(String p1, String p2){
        //Returns null on failure, otherwise returns the deleted record
        Record temp = getMPRecord(p1, p2);
        if(temp == null){
            System.out.println("No MP record to delete for: "+p1+" vs "+p2);
            return null;
        }
        try(PreparedStatement stm = DBC.prepareStatement(DELETE_MP_RECORD)) {
            //p1 p2 p1 p2
            stm.setString(1, p1);
            stm.setString(2, p2);
            stm.setString(3, p2);
            stm.setString(4, p1);
            stm.executeUpdate();
            System.out.println("Deleted MP record for: "+p1+" vs "+ p2);
            TimeUnit.SECONDS.sleep(1);
        } catch (SQLException | InterruptedException throwables) {
            throwables.printStackTrace();
        }


        return temp;
    }

    public void update(Record oldRecord, Record newRecord) {
/*"UPDATE RECORDS set player1 = ?, p1Score = ? WHERE (player1 = ? AND player2 IS NULL)";
    "UPDATE RECORDS set player1 = ?, player2 = ?, p1Score = ?, p2Score = ? WHERE ((player1 = ? AND player2 = ?) OR (player1 = ? AND player2 = ?))";
   */

        PreparedStatement stm;
        try {
            if (oldRecord.getP2() == null) {
                System.out.println("Updating SP record for: " + oldRecord.getP1() + " with score: " + oldRecord.getP1Score() + " To: " + newRecord.getP1() + " with score: " + newRecord.getP1Score());
                stm = DBC.prepareStatement(UPDATE_SP_RECORD);
                stm.setString(1, newRecord.getP1());
                stm.setInt(2, newRecord.getP1Score());
                stm.setString(3, oldRecord.getP1());
            }else{
                System.out.println("Updating MP record with players: "+oldRecord.getP1()+" vs "+oldRecord.getP2() + " with scores:" + oldRecord.getP1Score() + " vs " + oldRecord.getP2Score());
                System.out.println("To: "+newRecord.getP1()+" vs "+newRecord.getP2() + " with scores:" + newRecord.getP1Score() + " vs " + newRecord.getP2Score() );
                stm = DBC.prepareStatement(UPDATE_MP_RECORD);
                stm.setString(1, newRecord.getP1());
                stm.setString(2, newRecord.getP2());
                stm.setInt(3, newRecord.getP1Score());
                stm.setInt(4, newRecord.getP2Score());
                stm.setString(5, oldRecord.getP1());
                stm.setString(6, oldRecord.getP2());
                stm.setString(7,oldRecord.getP2());
                stm.setString(8, oldRecord.getP1());
            }
            stm.executeUpdate();
            stm.close();
            TimeUnit.SECONDS.sleep(1);
        } catch (SQLException | InterruptedException throwables) {
            throwables.printStackTrace();
        }
    }


}
