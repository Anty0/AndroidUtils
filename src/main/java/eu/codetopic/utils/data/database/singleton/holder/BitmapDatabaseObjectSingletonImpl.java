/*
 * ApplicationPurkynka
 * Copyright (C)  2017  anty
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.codetopic.utils.data.database.singleton.holder;

import eu.codetopic.utils.data.database.DatabaseBase;
import eu.codetopic.utils.data.database.holder.BitmapDatabaseObject;
import eu.codetopic.utils.data.database.singleton.SingletonDatabase;
import eu.codetopic.utils.data.getter.DatabaseDaoGetter;
import proguard.annotation.Keep;
import proguard.annotation.KeepClassMemberNames;
import proguard.annotation.KeepClassMembers;
import proguard.annotation.KeepName;

@Keep
@KeepName
@KeepClassMembers
@KeepClassMemberNames
public class BitmapDatabaseObjectSingletonImpl extends BitmapDatabaseObject {

    public static DatabaseDaoGetter<BitmapDatabaseObjectSingletonImpl, Long> getter =
            SingletonDatabase.getGetterFor(BitmapDatabaseObjectSingletonImpl.class);

    public BitmapDatabaseObjectSingletonImpl() {
        super(BitmapHolderSingletonImpl.class);
    }

    @Override
    protected DatabaseBase getDatabase() {
        return SingletonDatabase.getInstance();
    }
}
