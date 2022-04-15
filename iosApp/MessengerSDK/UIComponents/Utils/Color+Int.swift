import Foundation
import SwiftUI

extension Color {
    init(red: Int, green: Int, blue: Int, opacity: Double = 1.0) {
        self.init(
            red: Double(red) / 255.0,
            green: Double(blue) / 255.0,
            blue: Double(blue) / 255.0,
            opacity: opacity
        )
    }
}
