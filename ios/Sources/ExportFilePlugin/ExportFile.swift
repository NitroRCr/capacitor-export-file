import Foundation

@objc public class ExportFile: NSObject {
    @objc public func echo(_ value: String) -> String {
        print(value)
        return value
    }
}
