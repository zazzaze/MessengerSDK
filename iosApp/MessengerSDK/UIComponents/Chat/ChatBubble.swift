import SwiftUI

public struct ChatBubble<Content>: View where Content: View {
    let direction: ChatBubbleShape.Direction
    let content: () -> Content
    init(
        direction: ChatBubbleShape.Direction,
        @ViewBuilder content: @escaping () -> Content
    ) {
        self.content = content
        self.direction = direction
    }

    public var body: some View {
        HStack {
            if direction.isRight {
                Spacer()
            }
            content().clipShape(ChatBubbleShape(direction: direction))
            if direction.isLeft {
                Spacer()
            }
        }
        .padding(direction.isLeft ? .leading : .trailing, 10)
        .padding(direction.isRight ? .leading : .trailing, 10)
    }
}

public struct ChatBubbleShape: Shape {
    enum Direction: Equatable {
        case left(drawTail: Bool)
        case right(drawTail: Bool)

        var isLeft: Bool {
            return self == .left(drawTail: true) || self == .left(drawTail: false)
        }

        var isRight: Bool {
            return self == .right(drawTail: true) || self == .right(drawTail: false)
        }
    }

    let direction: Direction

    public func path(in rect: CGRect) -> Path {
        switch direction {
        case .left(let drawTail):
            return drawTail ? getLeftBubblePath(in: rect) : getNoneBubblePath(in: rect)
        case .right(let drawTail):
            return drawTail ? getRightBubblePath(in: rect) : getNoneBubblePath(in: rect)

        }
    }

    private func getLeftBubblePath(in rect: CGRect) -> Path {
        let width = rect.width
        let height = rect.height
        let path = Path { p in
            p.move(to: CGPoint(x: 25, y: height))
            p.addLine(to: CGPoint(x: width - 20, y: height))
            p.addCurve(to: CGPoint(x: width, y: height - 20),
                       control1: CGPoint(x: width - 8, y: height),
                       control2: CGPoint(x: width, y: height - 8))
            p.addLine(to: CGPoint(x: width, y: 20))
            p.addCurve(to: CGPoint(x: width - 20, y: 0),
                       control1: CGPoint(x: width, y: 8),
                       control2: CGPoint(x: width - 8, y: 0))
            p.addLine(to: CGPoint(x: 21, y: 0))
            p.addCurve(to: CGPoint(x: 4, y: 20),
                       control1: CGPoint(x: 12, y: 0),
                       control2: CGPoint(x: 4, y: 8))
            p.addLine(to: CGPoint(x: 4, y: height - 11))
            p.addCurve(to: CGPoint(x: 0, y: height),
                       control1: CGPoint(x: 4, y: height - 1),
                       control2: CGPoint(x: 0, y: height))
            p.addLine(to: CGPoint(x: -0.05, y: height - 0.01))
            p.addCurve(to: CGPoint(x: 11.0, y: height - 4.0),
                       control1: CGPoint(x: 4.0, y: height + 0.5),
                       control2: CGPoint(x: 8, y: height - 1))
            p.addCurve(to: CGPoint(x: 25, y: height),
                       control1: CGPoint(x: 16, y: height),
                       control2: CGPoint(x: 20, y: height))

        }

        return path
    }

    private func getRightBubblePath(in rect: CGRect) -> Path {
        let width = rect.width
        let height = rect.height
        let path = Path { p in
            p.move(to: CGPoint(x: 25, y: height))
            p.addLine(to: CGPoint(x:  20, y: height))
            p.addCurve(to: CGPoint(x: 0, y: height - 20),
                       control1: CGPoint(x: 8, y: height),
                       control2: CGPoint(x: 0, y: height - 8))
            p.addLine(to: CGPoint(x: 0, y: 20))
            p.addCurve(to: CGPoint(x: 20, y: 0),
                       control1: CGPoint(x: 0, y: 8),
                       control2: CGPoint(x: 8, y: 0))
            p.addLine(to: CGPoint(x: width - 21, y: 0))
            p.addCurve(to: CGPoint(x: width - 4, y: 20),
                       control1: CGPoint(x: width - 12, y: 0),
                       control2: CGPoint(x: width - 4, y: 8))
            p.addLine(to: CGPoint(x: width - 4, y: height - 11))
            p.addCurve(to: CGPoint(x: width, y: height),
                       control1: CGPoint(x: width - 4, y: height - 1),
                       control2: CGPoint(x: width, y: height))
            p.addLine(to: CGPoint(x: width + 0.05, y: height - 0.01))
            p.addCurve(to: CGPoint(x: width - 11, y: height - 4),
                       control1: CGPoint(x: width - 4, y: height + 0.5),
                       control2: CGPoint(x: width - 8, y: height - 1))
            p.addCurve(to: CGPoint(x: width - 25, y: height),
                       control1: CGPoint(x: width - 16, y: height),
                       control2: CGPoint(x: width - 20, y: height))

        }

        return path
    }

    private func getNoneBubblePath(in rect: CGRect) -> Path {
        let width = rect.width
        let height = rect.height
        let path = Path { p in
            p.move(to: CGPoint(x: 25, y: height))
            p.addLine(to: CGPoint(x:  20, y: height))
            p.addCurve(to: CGPoint(x: 0, y: height - 20),
                       control1: CGPoint(x: 8, y: height),
                       control2: CGPoint(x: 0, y: height - 8))
            p.addLine(to: CGPoint(x: 0, y: 20))
            p.addCurve(to: CGPoint(x: 20, y: 0),
                       control1: CGPoint(x: 0, y: 8),
                       control2: CGPoint(x: 8, y: 0))
            p.addLine(to: CGPoint(x: width - 25, y: 0))
            p.addCurve(to: CGPoint(x: width, y: 20),
                       control1: CGPoint(x: width - 8, y: 0),
                       control2: CGPoint(x: width, y: 8))
            p.addLine(to: CGPoint(x: width, y: height - 20))
            p.addCurve(to: CGPoint(x: width - 20, y: height),
                       control1: CGPoint(x: width, y: height - 8),
                       control2: CGPoint(x: width - 8, y: height))
            p.addLine(to: CGPoint(x: 25, y: height))
        }
        return path
    }
}

#if DEBUG
struct ChatBubble_Preview: PreviewProvider {
    static var previews: some View {
        ChatBubble(direction: .left(drawTail: true)) {
            Text("Some String")
                .padding(.vertical, 10)
                .padding(.horizontal, 15)
                .background(.blue)
                .foregroundColor(.white)
        }
        .previewLayout(.sizeThatFits)

        ChatBubble(direction: .right(drawTail: true)) {
            Text("Some String")
                .padding(.vertical, 10)
                .padding(.horizontal, 15)
                .background(.green)
                .foregroundColor(.white)
        }
        .previewLayout(.sizeThatFits)

        ChatBubble(direction: .left(drawTail: false)) {
            Text("Some String")
                .padding(.vertical, 10)
                .padding(.horizontal, 15)
                .background(.green)
                .foregroundColor(.white)
        }
        .previewLayout(.sizeThatFits)

        ChatBubble(direction: .right(drawTail: false)) {
            Text("Some String")
                .padding(.vertical, 10)
                .padding(.horizontal, 15)
                .background(.green)
                .foregroundColor(.white)
        }
        .previewLayout(.sizeThatFits)

        VStack {
            Spacer()
            ChatBubble(direction: .left(drawTail: false)) {
                Text("Hello!")
                    .padding(.vertical, 10)
                    .padding(.horizontal, 15)
                    .background(.blue)
                    .foregroundColor(.white)
            }
            ChatBubble(direction: .left(drawTail: true)) {
                Text("How are you?")
                    .padding(.vertical, 10)
                    .padding(.horizontal, 15)
                    .background(.blue)
                    .foregroundColor(.white)
            }
            ChatBubble(direction: .right(drawTail: false)) {
                Text("Hello!")
                    .padding(.vertical, 10)
                    .padding(.horizontal, 15)
                    .background(.green)
                    .foregroundColor(.white)
            }
            ChatBubble(direction: .right(drawTail: true)) {
                Text("I'm ok. And You?")
                    .padding(.vertical, 10)
                    .padding(.horizontal, 15)
                    .background(.green)
                    .foregroundColor(.white)
                    .transition(.slide)
            }
        }
    }
}
#endif
