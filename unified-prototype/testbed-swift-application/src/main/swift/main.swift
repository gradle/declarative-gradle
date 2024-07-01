import Foundation
import SwiftUtil

#if swift(>=5.0)
print("Hello from Swift >=5.0")
#else
print("Hello from unknown Swift version")
#endif
print(Utils().welcome)
