/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.commons.metadata.mqleditor.editor.service.util;

import org.junit.Assert;
import org.junit.Test;
import org.pentaho.commons.metadata.mqleditor.AggType;
import org.pentaho.commons.metadata.mqleditor.MqlDomain;
import org.pentaho.metadata.model.Domain;
import org.pentaho.metadata.model.concept.types.AggregationType;
import org.pentaho.metadata.model.concept.types.LocalizedString;
import org.pentaho.metadata.repository.IMetadataDomainRepository;
import org.pentaho.metadata.repository.InMemoryMetadataDomainRepository;

import java.util.List;

public class MQLEditorServiceDelegateTest {
  @Test
  public void convertNewThinAggregationTypeTest() {
    MQLEditorServiceDelegate mqlESD = new MQLEditorServiceDelegate();

    Assert.assertEquals( mqlESD.convertNewThinAggregationType( null ), AggType.NONE );

    Assert.assertEquals( mqlESD.convertNewThinAggregationType( AggregationType.COUNT ), AggType.COUNT );

    Assert.assertEquals( mqlESD.convertNewThinAggregationType( AggregationType.COUNT_DISTINCT ), AggType.COUNT_DISTINCT );

    Assert.assertEquals( mqlESD.convertNewThinAggregationType( AggregationType.AVERAGE ), AggType.AVERAGE );

    Assert.assertEquals( mqlESD.convertNewThinAggregationType( AggregationType.MINIMUM ), AggType.MIN );

    Assert.assertEquals( mqlESD.convertNewThinAggregationType( AggregationType.MAXIMUM ), AggType.MAX );

    Assert.assertEquals( mqlESD.convertNewThinAggregationType( AggregationType.SUM ), AggType.SUM );

    Assert.assertEquals( mqlESD.convertNewThinAggregationType( AggregationType.NONE ), AggType.NONE );
  }

  @Test
  public void testPPP_3873() throws Exception {
    IMetadataDomainRepository repo = new InMemoryMetadataDomainRepository();

    for (int i = 0; i < 500; i++) {
      Domain domain = new Domain();
      LocalizedString name = new LocalizedString();
      name.setString("US", String.valueOf(i + 1));
      domain.setId("name" + String.valueOf(i + 1));
      domain.setName(name);
      repo.storeDomain(domain, false);
    }

    MQLEditorServiceDelegate service = new MQLEditorServiceDelegate(repo);

    Runnable readAction = new Runnable() {
      public void run() {
        List<MqlDomain> mqlDomains = service.getMetadataDomains();
          for (int i = 0; i < mqlDomains.size(); i += 10) {
            mqlDomains.remove(i);
          }
        }
      };
      Thread reader = new Thread(readAction);
      reader.start();

      List<MqlDomain> mqlDomains = service.refreshMetadataDomains();
      for (final MqlDomain domain : mqlDomains) {
        Runnable r = new Runnable() {
          public void run() {
            while (true) {
              domain.getName();
            }
          }
        };
      Thread thr = new Thread(r);
      thr.start();
      }
    }
}
