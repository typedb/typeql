/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

use crate::common::Result;

pub trait Validatable: Sized {
    fn validate(&self) -> Result;

    fn validated(self) -> Result<Self> {
        self.validate().map(|_| self)
    }
}
