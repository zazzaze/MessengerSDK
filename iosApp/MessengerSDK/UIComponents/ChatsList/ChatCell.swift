import SwiftUI

struct ChatCell: View {
    let model: CellModel

    var body: some View {
        HStack {
            buildTextAvatarView(string: model.title)
                .clipShape(Circle())
            VStack(alignment: .leading, spacing: 10) {
                Text(model.title)
                    .lineLimit(1)
                Text(model.message ?? "No messages yet")
                    .font(.caption)
                    .lineLimit(1)
            }
            Spacer()
            if model.unreadMessagesCount > 0 {
                notificationCircle(count: model.unreadMessagesCount)
            }
        }
        .padding(.horizontal, 20)
        .padding(.vertical, 10)
    }

    @ViewBuilder func buildTextAvatarView(string: String) -> some View {
        VStack(alignment: .center) {
            Text(getCharactersFromTitle(title: string).uppercased())
                .padding(20)
                .foregroundColor(.white)
        }
        .background(.green)
    }

    @ViewBuilder func notificationCircle(count: Int) -> some View {
        VStack(alignment: .center) {
            Text(count > 99 ? "99+" : "\(count)")
                .foregroundColor(.white)
                .font(.caption)
                .padding(7)
        }
        .background(.blue)
        .clipShape(Circle())
    }

    private func getCharactersFromTitle(title: String) -> String {
        let components = title.split(separator: " ").map { $0.trimmingCharacters(in: .whitespacesAndNewlines) }
        if components.isEmpty {
            return ""
        }
        else if components.count == 1 {
            if components.first!.isEmpty {
                return ""
            }
            if components.first!.count > 1 {
                return "\(components.first!.first!)\(components.first![1])"
            } else {
                return "\(components.first!.first  ?? Character(""))"
            }
        }
        else {
            return "\(components.first?.first ?? Character(""))\(components[1].first ?? Character(""))"
        }
    }

    struct CellModel {
        let image: URL?
        let title: String
        let message: String?
        let unreadMessagesCount: Int
    }
}

private extension String {
    var length: Int {
        return count
    }

    subscript (i: Int) -> String {
        return self[i ..< i + 1]
    }

    func substring(fromIndex: Int) -> String {
        return self[min(fromIndex, length) ..< length]
    }

    func substring(toIndex: Int) -> String {
        return self[0 ..< max(0, toIndex)]
    }

    subscript (r: Range<Int>) -> String {
        let range = Range(
            uncheckedBounds: (lower: max(0, min(length, r.lowerBound)),
            upper: min(length, max(0, r.upperBound)))
        )
        let start = index(startIndex, offsetBy: range.lowerBound)
        let end = index(start, offsetBy: range.upperBound - range.lowerBound)
        return String(self[start ..< end])
    }
}

struct ChatCell_Previews: PreviewProvider {
    static var previews: some View {
        ChatCell(
            model: .init(
                image: nil,
                title: "ABC ABC",
                message: "My new message",
                unreadMessagesCount: 2
            )
        )
            .previewLayout(.sizeThatFits)

        ChatCell(
            model: .init(
                image: nil,
                title: "ABCfsadfkjsdakl;fjkas;ljfkjsal;kdfjlsdakjf;las ABC",
                message: "My new message fksdjfkldasjkfldals;",
                unreadMessagesCount: 2
            )
        )
            .previewLayout(.sizeThatFits)

        ChatCell(
            model: .init(
                image: nil,
                title: "sdfa",
                message: "My new message fksdjfkldasjkfldals;",
                unreadMessagesCount: 2
            )
        )
            .previewLayout(.sizeThatFits)

        ChatCell(
            model: .init(
                image: nil,
                title: "sdfa",
                message: "My new message fksdjfkldasjkfldals;",
                unreadMessagesCount: 100
            )
        )
            .previewLayout(.sizeThatFits)

        ChatCell(
            model: .init(
                image: nil,
                title: "sdfa",
                message: "My new message fksdjfkldasjkfldals;",
                unreadMessagesCount: 0
            )
        )
            .previewLayout(.sizeThatFits)

        ChatCell(
            model: .init(
                image: nil,
                title: "sdfa",
                message: nil,
                unreadMessagesCount: 0
            )
        )
            .previewLayout(.sizeThatFits)
    }
}
