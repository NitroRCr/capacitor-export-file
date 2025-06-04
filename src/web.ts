import { WebPlugin } from '@capacitor/core';

import type { ExportFilePlugin } from './definitions';

export class ExportFileWeb extends WebPlugin implements ExportFilePlugin {
  async exportFile(options: { uri: string; filename?: string }): Promise<{ uri: string }> {
    const { uri, filename = 'download_file' } = options;

    try {
      const response = await fetch(uri);
      const blob = await response.blob();

      const objectURL = URL.createObjectURL(blob);

      const anchor = document.createElement('a');
      anchor.href = objectURL;
      anchor.download = filename;
      anchor.style.display = 'none';

      document.body.appendChild(anchor);
      anchor.click();

      // Clean up
      document.body.removeChild(anchor);
      URL.revokeObjectURL(objectURL);

      return { uri: objectURL };
    } catch (error) {
      throw new Error(`Failed to export file: ${(error as Error).message}`);
    }
  }
}
