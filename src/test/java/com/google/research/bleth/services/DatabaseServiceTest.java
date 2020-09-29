package com.google.research.bleth.services;

import com.google.appengine.api.datastore.dev.LocalDatastoreService;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.research.bleth.simulator.Board;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static com.google.common.truth.Truth.assertThat;

@RunWith(MockitoJUnitRunner.class)
public final class DatabaseServiceTest extends TestCase {

    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig()
                    .setAutoIdAllocationPolicy(LocalDatastoreService.AutoIdAllocationPolicy.SEQUENTIAL));

    private static String firstSimulationId = "test-tracing-sim-id-1";
    private static int roundOne = 1;

    @Before
    public void setUp() {
        helper.setUp();
    }

    @Test
    public void writeAndThenReadEmptyRealBoardStateShouldGetSameBoardState() {

        DatabaseService db = DatabaseService.getInstance();
        String emptyRealBoardState = new Board(2, 2).getState();

        db.writeRealBoardState(firstSimulationId, roundOne, emptyRealBoardState);
        String outputRealBoardState = db.getRealBoardState(firstSimulationId, roundOne);

        assertThat(emptyRealBoardState).isEqualTo(outputRealBoardState);
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    @AfterAll
    static void clearDatastore() {
        DatabaseService db = DatabaseService.getInstance();
        db.deleteAllSimulationRealBoardStates(firstSimulationId);
        db.deleteAllSimulationEstimatedBoardStates(firstSimulationId);
    }
}
