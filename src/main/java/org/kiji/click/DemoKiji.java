/**
 * (c) Copyright 2013 WibiData, Inc.
 *
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kiji.click;

import java.io.IOException;

import org.kiji.click.ItemPurchase;

import org.kiji.schema.EntityId;
import org.kiji.schema.Kiji;
import org.kiji.schema.KijiDataRequest;
import org.kiji.schema.KijiRowData;
import org.kiji.schema.KijiTable;
import org.kiji.schema.KijiTableReader;
import org.kiji.schema.KijiTableWriter;
import org.kiji.schema.KijiURI;

/**
 * A demonstration of the Kiji API.
 *
 * Here, we assume the table description is described by json,
 * it is in table_desc.json.
 */
public final class DemoKiji {
  /**
   * Private constructor for utility class.
   */
  private DemoKiji() {
    // No-op private constructor. This class's main method
    // should be run from the command line.
  }

  /**
   * Main method (run from command line).
   *
   * @param args Command line arguments.
   * @throws IOException If opening the kiji fails.
   */
  public static void main(String[] args) throws IOException {
    // First we define some constants that we use for our demo.

    final String USER = "Bob";

    // The name of the table we will create in this demo.
    final String tableName = "users";

    // Kiji instances are specified by KijiURIs, formatted as below.
    // This is the default kiji instance.
    final String uri = "kiji-cassandra://localhost:2181/localhost/9042/cas";
    final KijiURI kijiURI = KijiURI.newBuilder(uri).build();

    // Open the kiji specified.
    Kiji kiji = Kiji.Factory.open(kijiURI);

    // Always surround with a try {} finally{} so the kiji gets released,
    // no matter what happens.
    try {
      // Get a handle to the table.
      KijiTable table = kiji.openTable(tableName);

      // Get the entity ID, according to this table, of the user we are
      // demonstrating with.
      EntityId entityId = table.getEntityId(USER);

      // ----- Write a row to the table. -----
      // Get a TableWriter for our table.
      KijiTableWriter tableWriter = table.openTableWriter();
      // Surround with a try/finally so the tablewriter gets closed.
      try {
        System.out.println("Putting user " + USER + " into table.");
        tableWriter.put(entityId, "info", "name", USER);

        // Let's also write an item purchase!
        ItemPurchase itemPurchase = ItemPurchase.newBuilder()
            .setItemId(10L)
            .setItemName("Socks")
            .setPurchaseAmount(5.00)
            .setTransactionId(1234555L)
            .build();

        tableWriter.put(entityId, "interactions", "purchases", itemPurchase);

        // Flush the write to the table, since this is a demo and
        // we are not concerned about efficiency, we just want to
        // show that the cell got written successfully.
        tableWriter.flush();
      } finally {
        tableWriter.close();
      }

      // ----- Read a row from the table. -----
      // Get a TableReader for our table.
      KijiTableReader tableReader = table.openTableReader();
      // Surround with a try/finally so the tablereader gets closed.
      try {
        // Build a DataRequest for the row we want.
        KijiDataRequest dataRequest = KijiDataRequest.create("info", "name");
        KijiRowData result = tableReader.get(entityId, dataRequest);
        String name = result.getMostRecentValue("info", "name").toString();
        System.out.println("Read username " + name + " from table.");
      } finally {
        tableReader.close();
      }
      table.release();
    } finally {
      kiji.release();
    }
  }
}
