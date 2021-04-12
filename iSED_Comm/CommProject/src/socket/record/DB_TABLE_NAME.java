package socket.record;

public class DB_TABLE_NAME {
    /************************Linux Server*****************************************/

    String SELECTED_TEST_TABLE_FULL = "`vibrant_america_information`.`selected_test_list`";
    String SAMPLE_INFO_TABLE_FULL = "`vibrant_america_information`.`sample_data`";
    String TEST_INFO_TABLE_FULL = "`vibrant_america_information`.`test_list`";

    String NEW_RESULT_TRACKING_TABLE_FULL = "`vibrant_america_information`.`new_result_test_list`";

    String TEST_INFO_TABLE_FULL_mod = "`vibrant_america_information`.`test_list`";

    String PATIENT_DETAIL_TABLE_FULL = "`vibrant_america_information`.`patient_details`";

    String SELECTED_TEST_TABLE_ONLY = "selected_test_list";


    String TEST_RESULT_SCHEMA_NAME = "`vibrant_america_test_result`";
    String TEST_INFO_SCHEMA_NAME = "`vibrant_america_information`";


    String PENDING_TEST_TABLE_FULL = "`vibrant_america_information`.`pending_test_list`";
    String PENDING_TEST_TABLE_ONLY = "pending_test_list";


    String CUSTOMER_DETAILS_TABLE_FULL = "`vibrant_america_information`.`customer_details`";

    String TEST_MASTER_LIST = "`vibrant_america_information`.`report_master_list`";

    String TNP_CODE_TABLE_FULL = "`vibrant_america_information`.`internal_error_codes`";

    static public String RECEIVE_STATUS_CHECK_TABLE = "`commproject`.`receive_status_check`";
    static public String RECEIVE_STATUS_TEST_MODE_CHECK_TABLE = "`lis`.`receive_status_check_test_mode`";
    public static String RESULT_SAVING_STATUS_TABLE = "`lis`.`reuslt_saving_status`";

    static public String PROD_SAMPLE_RACK_INFO = "`vibrant_america_information`.`prod_sample_rack_info`";
    static public String SAMPLE_DATA = "`vibrant_america_information`.`sample_data`";
    static public String PROD_SAMPLE_STORAGE = "`vibrant_america_information`.`prod_sample_storage`";
    static public String PROD_SAMPLE_STORAGE_DELETED = "`vibrant_america_information`.`prod_sample_storage_deleted`";
    ////Local DB////
    String na_test_list_local = "`lis_local_db`.`na_test_list_local`";
    String hemotology_manual_result = "`lis_local_db`.`na_test_list_local`";

    String db_address = "jdbc:mysql://localhost:3306/";
    String user = "root";
    String password = "root";

    //String db_address = "jdbc:mysql://localhost:3306/test";
    //String user ="root";
    //String password = "000028";

    String db_address_local = "jdbc:mysql://localhost:3306/test";
    String user_local ="root";
    String password_local = "000028";


    public static final boolean ENABLE_SIMULATION = false;

    public static final String communicateLogPath = "C:\\Users\\Louis\\Desktop\\RocheLog\\";

}

