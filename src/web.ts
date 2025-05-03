import { WebPlugin } from '@capacitor/core';

import type { ExportFilePlugin } from './definitions';

export class ExportFileWeb extends WebPlugin implements ExportFilePlugin {
  async exportFile(): Promise<{ uri: string }> {
    throw this.unimplemented('Only support Android');
  }
}
