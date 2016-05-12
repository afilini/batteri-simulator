/*
  Copyright (C) 2016 Alekos Filini (alekos.filini@gmail.com)

  This program is free software; you can redistribute it and/or
  modify it under the terms of the GNU General Public License
  as published by the Free Software Foundation; either version 2
  of the License, or (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program; if not, write to the Free Software
  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

package cms;

import org.junit.Assert;
import org.junit.Test;
import utils.Status;

import java.util.LinkedList;

public class ResultSenderTest {

    @Test
    public void testSend() throws Exception {
        ResultSender sender = new ResultSender("http://localhost/bcms/utils/testSenderMethod.php");

        LinkedList<Status> stati = new LinkedList<>();
        stati.add(new Status("name", false, 100));

        String response = sender.send(stati);

        Assert.assertEquals("OK", response);
    }

    @Test(expected = java.io.IOException.class)
    public void testSendWrongURL() throws Exception {
        ResultSender sender = new ResultSender("http://localhost:22/bcms/utils/testSenderMethod.php");

        LinkedList<Status> stati = new LinkedList<>();
        stati.add(new Status("name", false, 100));

        String response = sender.send(stati);

        Assert.assertEquals("OK", response);
    }
}