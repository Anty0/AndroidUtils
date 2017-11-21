/*
 * utils
 * Copyright (C)   2017  anty
 *
 * This program is free  software: you can redistribute it and/or modify
 * it under the terms  of the GNU General Public License as published by
 * the Free Software  Foundation, either version 3 of the License, or
 * (at your option) any  later version.
 *
 * This program is distributed in the hope that it  will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied  warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.   See the
 * GNU General Public License for more details.
 *
 * You  should have received a copy of the GNU General Public License
 * along  with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.codetopic.utils.data.database.singleton.holder;

import android.support.annotation.Keep;

import eu.codetopic.utils.data.database.holder.BitmapHolder;
import eu.codetopic.utils.data.getter.DatabaseDaoGetter;

public class BitmapHolderSingletonImpl extends BitmapHolder<BitmapDatabaseObjectSingletonImpl> {

    /**
     * @hide
     */
    @Keep
    public BitmapHolderSingletonImpl() {
    }

    @Keep
    public BitmapHolderSingletonImpl(Long objectId) {
        super(objectId);
    }

    @Override
    public DatabaseDaoGetter<BitmapDatabaseObjectSingletonImpl, Long> getDaoGetter() {
        return BitmapDatabaseObjectSingletonImpl.getter;
    }
}
